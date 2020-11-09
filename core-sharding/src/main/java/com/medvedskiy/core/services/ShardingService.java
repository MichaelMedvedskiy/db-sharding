package com.medvedskiy.core.services;


import com.medvedskiy.core.exceptions.UndefinedBehaviorException;
import com.medvedskiy.repository.dao.AssociationEntity;
import com.medvedskiy.repository.dao.PaymentEntity;
import com.medvedskiy.repository.repositories.association.AssociationEntityRepository;
import com.medvedskiy.repository.repositories.payment.db1.PaymentEntityDB1Repository;
import com.medvedskiy.repository.repositories.payment.db2.PaymentEntityDB2Repository;
import com.medvedskiy.repository.repositories.payment.db3.PaymentEntityDB3Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Shard list of Payments to 3 databases
 *
 * @see com.medvedskiy.core.models.Payment
 */
@Service
public class ShardingService {

    private final AssociationEntityRepository associationEntityRepository;

    private final ConverterService converterService;

    private final PaymentEntityDB1Repository firstDatabasePaymentRepository;

    private final PaymentEntityDB2Repository secondDatabasePaymentRepository;

    private final PaymentEntityDB3Repository thirdDatabasePaymentRepository;



    public ShardingService(
            AssociationEntityRepository associationEntityRepository,
            ConverterService converterService,
            PaymentEntityDB1Repository firstDatabasePaymentRepository,
            PaymentEntityDB2Repository secondDatabasePaymentRepository,
            PaymentEntityDB3Repository thirdDatabasePaymentRepository

    ) {
        this.associationEntityRepository = associationEntityRepository;
        this.converterService = converterService;
        this.firstDatabasePaymentRepository = firstDatabasePaymentRepository;
        this.secondDatabasePaymentRepository = secondDatabasePaymentRepository;
        this.thirdDatabasePaymentRepository = thirdDatabasePaymentRepository;

    }

    private final int databaseCount = 3;

    /**
     * Gets Payment list
     * Clusters Payments by same sender Id
     * Gets DB Index for each cluster
     * Merges all Payments with same DB Index into lists
     * Persists lists into corresponding DBs
     *
     * @param payments to insert in db
     * @return whether successful
     * @throws UndefinedBehaviorException
     */
    public boolean insertPayments(List<com.medvedskiy.core.models.Payment> payments) throws UndefinedBehaviorException {
        //Clusters Payments by same sender Id
        Map<Long, List<com.medvedskiy.core.models.Payment>> clusterizedPayments = clusterizePayments(payments);
        Set<Long> passedSenderIds = new HashSet<>(clusterizedPayments.keySet());

        //Gets DB Index for each cluster
        Map<Long, Integer> existingSenderIdsIndexed = getDbIndexesForExistingSenders(clusterizedPayments.keySet());
        passedSenderIds.removeAll(existingSenderIdsIndexed.keySet());
        Set<Long> sendersNotInSystem = (new HashSet<>(passedSenderIds));
        Map<Long, Integer> newSenderIdsIndexes = getDbIndexesForNewSenders(sendersNotInSystem);

        associateSendersWithDatabase(newSenderIdsIndexes);

        Map<Long, Integer> allSenderIdsIndexes = Stream.concat(
                existingSenderIdsIndexed.entrySet().stream(),
                newSenderIdsIndexes.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        long greatestDBIndex = allSenderIdsIndexes.values().stream().max(Integer::compareTo).orElseThrow();

        if (greatestDBIndex > databaseCount - 1) {
            throw new UndefinedBehaviorException(String.format("Scheduled pasting in db#%d, but max db index is %d",
                    greatestDBIndex, databaseCount - 1));
        }

        //Merges all Payments with same DB Index into lists
        Map<Integer, List<com.medvedskiy.core.models.Payment>> listsForDatabases =
                mergeClustersForInsert(clusterizedPayments, allSenderIdsIndexes);

        //Persists lists into corresponding DBs
        List<PaymentEntity> listForFirstDb = converterService.paymentDAOWrapper(listsForDatabases.get(0));
        persistInDB1(listForFirstDb);

        List<PaymentEntity> listForSecondDb = converterService.paymentDAOWrapper(listsForDatabases.get(1));
        persistInDB2(listForSecondDb);

        List<PaymentEntity> listForThirdDb = converterService.paymentDAOWrapper(listsForDatabases.get(2));
        persistInDB3(listForThirdDb);

        return true;
    }

    @Transactional("firstTransactionManager")
    void persistInDB1(List<PaymentEntity> toInsert) {
        if (!toInsert.isEmpty()) {
            firstDatabasePaymentRepository.save(toInsert);
        }
    }

    @Transactional("secondTransactionManager")
    void persistInDB2(List<PaymentEntity> toInsert) {
        if (!toInsert.isEmpty()) {
            secondDatabasePaymentRepository.save(toInsert);
        }
    }

    @Transactional("thirdTransactionManager")
    void persistInDB3(List<PaymentEntity> toInsert) {
        if (!toInsert.isEmpty()) {
            thirdDatabasePaymentRepository.save(toInsert);
        }
    }

    private Map<Long, List<com.medvedskiy.core.models.Payment>> clusterizePayments(List<com.medvedskiy.core.models.Payment> inputPayments) {
        Map<Long, List<com.medvedskiy.core.models.Payment>> clustersBySender = new HashMap<>();
        for (com.medvedskiy.core.models.Payment payment : inputPayments) {
            Long clusterKey = payment.sender();
            if (clustersBySender.containsKey(clusterKey)) {
                List<com.medvedskiy.core.models.Payment> currentCluster = clustersBySender.get(clusterKey);
                currentCluster.add(payment);
            } else {
                List<com.medvedskiy.core.models.Payment> currentCluster = new ArrayList<>();
                currentCluster.add(payment);
                clustersBySender.put(clusterKey, currentCluster);
            }
        }
        return clustersBySender;
    }

    private Map<Integer, List<com.medvedskiy.core.models.Payment>> mergeClustersForInsert(
            Map<Long, List<com.medvedskiy.core.models.Payment>> clusters,
            Map<Long, Integer> allSenderIdsIndexes
    ) {
        Map<Integer, List<com.medvedskiy.core.models.Payment>> insertMap = new HashMap<>();

        for (int i = 0; i < databaseCount; i++) {
            insertMap.put(i, new ArrayList<com.medvedskiy.core.models.Payment>());
        }

        for (Map.Entry<Long, List<com.medvedskiy.core.models.Payment>> cluster : clusters.entrySet()) {

            int dbNumber = allSenderIdsIndexes.get(cluster.getKey());
            List<com.medvedskiy.core.models.Payment> listOfDbNumber = insertMap.get(dbNumber);
            listOfDbNumber.addAll(cluster.getValue());
        }

        return insertMap;
    }

    private Map<Long, Integer> getDbIndexesForExistingSenders(Iterable<Long> senderIds) {
        Map<Long, Integer> senderIndex = new HashMap<>();
        for (Long senderId : senderIds) {
            AssociationEntity associationForSender = associationEntityRepository.findOne(senderId);
            if (associationForSender != null) {
                senderIndex.put(senderId, associationForSender.getDbId());
            }
        }
        return senderIndex;
    }

    private Map<Long, Integer> getDbIndexesForNewSenders(Iterable<Long> senderIds) {
        Map<Long, Integer> senderIndexes = new HashMap<>();
        for (Long senderId : senderIds) {
            int dbId = senderId.intValue() % databaseCount;
            senderIndexes.put(senderId, dbId);
        }
        return senderIndexes;
    }

    void associateSendersWithDatabase(Map<Long, Integer> dbIndexesForNewSenders) {
        List<AssociationEntity> associationEntities = new ArrayList<>();
        for (Map.Entry<Long, Integer> senderIndex : dbIndexesForNewSenders.entrySet()) {
            AssociationEntity association = new AssociationEntity();
            association.setSender(senderIndex.getKey());
            association.setDbId(senderIndex.getValue());
            associationEntities.add(association);
        }
        persistInDBAssociation(associationEntities);
        //associationTemplate.execute(status ->);
    }

    @Transactional("associationTransactionManager")
    void persistInDBAssociation(List<AssociationEntity> toInsert) {
        if (!toInsert.isEmpty()) {
            associationEntityRepository.save(toInsert);
        }
    }


}
