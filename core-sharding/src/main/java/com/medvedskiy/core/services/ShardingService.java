package com.medvedskiy.core.services;


import com.medvedskiy.repository.dao.Association;
import com.medvedskiy.repository.dao.PaymentEntity;
import com.medvedskiy.repository.repositories.AssociationDAORepository;
import com.medvedskiy.repository.repositories.PaymentEntityRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@PropertySource("classpath:sharding.properties")
@Service
public class ShardingService {

    private final AssociationDAORepository associationDAORepository;

    private final ConverterService converterService;

    private final PaymentEntityRepository firstDatabasePaymentRepository;

    private final PaymentEntityRepository secondDatabasePaymentRepository;

    private final PaymentEntityRepository thirdDatabasePaymentRepository;

    private final TransactionTemplate firstTemplate;

    private final TransactionTemplate secondTemplate;

    private final TransactionTemplate thirdTemplate;

    private final TransactionTemplate associationTemplate;

    public ShardingService(
            @Qualifier("associationDBRepository")
                    AssociationDAORepository associationDAORepository,
            ConverterService converterService,
            @Qualifier("firstDBRepository")
                    PaymentEntityRepository firstDatabasePaymentRepository,
            @Qualifier("secondDBRepository")
                    PaymentEntityRepository secondDatabasePaymentRepository,
            @Qualifier("thirdDBRepository")
                    PaymentEntityRepository thirdDatabasePaymentRepository,
            @Qualifier("firstTransactionTemplate")
                    TransactionTemplate firstTemplate,
            @Qualifier("secondTransactionTemplate")
                    TransactionTemplate secondTemplate,
            @Qualifier("thirdTransactionTemplate")
                    TransactionTemplate thirdTemplate,
            @Qualifier("associationTransactionTemplate")
                    TransactionTemplate associationTemplate
    ) {
        this.associationDAORepository = associationDAORepository;
        this.converterService = converterService;
        this.firstDatabasePaymentRepository = firstDatabasePaymentRepository;
        this.secondDatabasePaymentRepository = secondDatabasePaymentRepository;
        this.thirdDatabasePaymentRepository = thirdDatabasePaymentRepository;

        this.firstTemplate = firstTemplate;
        this.secondTemplate = secondTemplate;
        this.thirdTemplate = thirdTemplate;
        this.associationTemplate = associationTemplate;
    }

    // TODO: 08.11.2020 make env var
    private final int databaseCount = 3;

    public boolean insertPayments(List<com.medvedskiy.core.models.Payment> payments) {
        Map<Long, List<com.medvedskiy.core.models.Payment>> clusterizedPayments = clusterizePayments(payments);
        Set<Long> passedSenderIds = clusterizedPayments.keySet();

        Map<Long, Integer> existingSenderIdsIndexed = getDbIndexesForExistingSenders(clusterizedPayments.keySet());
        passedSenderIds.removeAll(existingSenderIdsIndexed.keySet());
        Set<Long> sendersNotInSystem = (new HashSet<>(passedSenderIds));
        Map<Long, Integer> newSenderIdsIndexes = getDbIndexesForNewSenders(sendersNotInSystem);

        associateSendersWithDatabase(newSenderIdsIndexes);

        Map<Long, Integer> allSenderIdsIndexes = Stream.concat(
                existingSenderIdsIndexed.entrySet().stream(),
                newSenderIdsIndexes.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<Integer, List<com.medvedskiy.core.models.Payment>> listsForDatabases = mergeClustersForInsert(clusterizedPayments, allSenderIdsIndexes);

        List<PaymentEntity> listForFirstDb = converterService.paymentDAOWrapper(listsForDatabases.get(0));
        if (!listForFirstDb.isEmpty()) {
            firstTemplate.execute(status -> firstDatabasePaymentRepository.save(listForFirstDb));
        }
        List<PaymentEntity> listForSecondDb = converterService.paymentDAOWrapper(listsForDatabases.get(1));
        if (!listForSecondDb.isEmpty()) {
            secondTemplate.execute(status -> secondDatabasePaymentRepository.save(listForSecondDb));
        }
        List<PaymentEntity> listForThirdDb = converterService.paymentDAOWrapper(listsForDatabases.get(2));
        if (!listForThirdDb.isEmpty()) {
            thirdTemplate.execute(status -> thirdDatabasePaymentRepository.save(listForThirdDb));
        }
        return true;
    }


    public Map<Long, List<com.medvedskiy.core.models.Payment>> clusterizePayments(List<com.medvedskiy.core.models.Payment> inputPayments) {
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

    public Map<Integer, List<com.medvedskiy.core.models.Payment>> mergeClustersForInsert(
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
            Association associationForSender = associationDAORepository.findOne(senderId);
            if (associationForSender != null) {
                senderIndex.put(senderId, associationForSender.getDbId());
            }
        }
        return senderIndex;
    }

    private Map<Long, Integer> getDbIndexesForNewSenders(Iterable<Long> senderIds) {
        Map<Long, Integer> senderIndex = new HashMap<>();
        for (Long senderId : senderIds) {
            int dbId = senderId.intValue() % databaseCount;
            senderIndex.put(senderId, dbId);
        }
        return senderIndex;
    }

    void associateSendersWithDatabase(Map<Long, Integer> dbIndexesForNewSenders) {
        List<Association> associationEntities = new ArrayList<>();
        for (Map.Entry<Long, Integer> senderIndex : dbIndexesForNewSenders.entrySet()) {
            Association association = new Association();
            association.setSender(senderIndex.getKey());
            association.setDbId(senderIndex.getValue());
            associationEntities.add(association);
        }

        associationTemplate.execute(status -> associationDAORepository.save(associationEntities));
    }


}
