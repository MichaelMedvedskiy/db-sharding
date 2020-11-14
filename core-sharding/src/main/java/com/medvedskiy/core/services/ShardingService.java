package com.medvedskiy.core.services;


import com.medvedskiy.core.converters.Converters;
import com.medvedskiy.core.exceptions.UndefinedBehaviorException;
import com.medvedskiy.core.models.Payment;
import com.medvedskiy.repository.dao.association.AssociationEntity;
import com.medvedskiy.repository.dao.payment.PaymentEntity;
import com.medvedskiy.repository.repositories.association.AssociationEntityRepository;
import com.medvedskiy.repository.repositories.payment.PaymentEntityRepository;
import com.medvedskiy.repository.tenanting.ThreadLocalStorage;
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
 * @see Payment
 */
@Service
public class ShardingService {

    private final AssociationEntityRepository associationEntityRepository;

    private final PaymentEntityRepository paymentEntityRepository;

    private final int databaseCount;

    public ShardingService(
            AssociationEntityRepository associationEntityRepository,
            PaymentEntityRepository paymentEntityRepository
    ) {
        this.associationEntityRepository = associationEntityRepository;
        this.paymentEntityRepository = paymentEntityRepository;
        this.databaseCount = ThreadLocalStorage.getDatabaseCount();
    }

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
    public boolean insertPayments(List<Payment> payments) throws UndefinedBehaviorException {
        //Clusters Payments by same sender Id
        Map<Long, List<Payment>> clusterizedPayments = clusterizePayments(payments);
        Set<Long> passedSenderIds = new HashSet<>(clusterizedPayments.keySet());

        //Gets DB Index for each cluster
        Map<Long, Integer> existingSenderIdsIndexed = getDbIndexesForExistingSenders(clusterizedPayments.keySet());
        passedSenderIds.removeAll(existingSenderIdsIndexed.keySet());
        Set<Long> sendersNotInSystem = new HashSet<>(passedSenderIds);
        Map<Long, Integer> newSenderIdsIndexes = getDbIndexesForNewSenders(sendersNotInSystem);

        associateSendersWithDatabase(newSenderIdsIndexes);

        Map<Long, Integer> allSenderIdsIndexes = Stream.concat(
                existingSenderIdsIndexed.entrySet().stream(),
                newSenderIdsIndexes
                        .entrySet()
                        .stream()
        )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        long greatestDBIndex = allSenderIdsIndexes.values().stream().max(Integer::compareTo).orElseThrow();

        if (greatestDBIndex > databaseCount - 1) {
            throw new UndefinedBehaviorException(
                    String.format("Scheduled pasting in db#%d, but max 0-based db index is %d",
                            greatestDBIndex, databaseCount - 1)
            );
        }

        //Merges all Payments with same DB Index into lists
        Map<Integer, List<Payment>> listsForDatabases =
                mergeClustersForInsert(clusterizedPayments, allSenderIdsIndexes);

        for (Integer dbIndex : listsForDatabases.keySet()) {
            ThreadLocalStorage.setTenantName(String.valueOf(dbIndex));
            List<PaymentEntity> listForCurrentDb = Converters.paymentDAOWrapper(listsForDatabases.get(dbIndex));
            if (!listForCurrentDb.isEmpty()) {
                paymentEntityRepository.saveAll(listForCurrentDb);
            }
        }

        return true;
    }

    private Map<Long, List<Payment>> clusterizePayments(
            List<Payment> inputPayments
    ) {
        Map<Long, List<Payment>> clustersBySender = new HashMap<>();

        for (Payment payment : inputPayments) {
            Long clusterKey = payment.sender();

            if (clustersBySender.containsKey(clusterKey)) {
                List<Payment> currentCluster = clustersBySender.get(clusterKey);
                currentCluster.add(payment);
            } else {
                List<Payment> currentCluster = new ArrayList<>();
                currentCluster.add(payment);
                clustersBySender.put(clusterKey, currentCluster);
            }
        }

        return clustersBySender;
    }

    private Map<Integer, List<Payment>> mergeClustersForInsert(
            Map<Long, List<Payment>> clusters,
            Map<Long, Integer> allSenderIdsIndexes
    ) {
        Map<Integer, List<Payment>> insertMap = new HashMap<>();

        for (int i = 0; i < databaseCount; i++) {
            insertMap.put(i, new ArrayList<Payment>());
        }

        for (Map.Entry<Long, List<Payment>> cluster : clusters.entrySet()) {

            int dbNumber = allSenderIdsIndexes.get(cluster.getKey());
            List<Payment> listOfDbNumber = insertMap.get(dbNumber);
            listOfDbNumber.addAll(cluster.getValue());
        }

        return insertMap;
    }

    private Map<Long, Integer> getDbIndexesForExistingSenders(Iterable<Long> senderIds) {
        Map<Long, Integer> senderIndexes = new HashMap<>();

        for (Long senderId : senderIds) {
            AssociationEntity associationForSender = associationEntityRepository.findById(senderId).orElse(null);
            if (associationForSender != null) {
                senderIndexes.put(senderId, associationForSender.getDbId());
            }
        }

        return senderIndexes;
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
    }

    @Transactional("associationTransactionManager")
    void persistInDBAssociation(List<AssociationEntity> toInsert) {
        if (!toInsert.isEmpty()) {
            associationEntityRepository.saveAll(toInsert);
        }
    }


}
