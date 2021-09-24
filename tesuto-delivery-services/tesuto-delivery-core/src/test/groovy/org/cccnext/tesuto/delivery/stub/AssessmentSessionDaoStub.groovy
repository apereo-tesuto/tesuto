package org.cccnext.tesuto.service.scoring

import org.apache.commons.collections.CollectionUtils
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession
import org.cccnext.tesuto.delivery.service.AssessmentSessionDao
import org.cccnext.tesuto.delivery.service.DeliverySearchParameters
import org.springframework.stereotype.Service
import sun.reflect.generics.reflectiveObjects.NotImplementedException

@Service
public class AssessmentSessionDaoStub implements AssessmentSessionDao {

    def assessmentSessions = [:]

    @Override
    String create(AssessmentSession session) {
        assessmentSessions[session.assessmentSessionId] = session
        return session.assessmentSessionId
    }

    List<String> create(List<AssessmentSession> sessions) {
        def ids = []
        sessions.each{
            ids.add(create(it))
        }
        return ids
    }

    @Override
    boolean delete(String id) {
        assessmentSessions[id] = null
        return true
    }

    @Override
    AssessmentSession find(String id) {
        return assessmentSessions[id]
    }

    @Override
    AssessmentSession save(AssessmentSession session) {
        assessmentSessions[session.assessmentSessionId] = session
        return assessmentSessions[session.assessmentSessionId]
    }

    @Override
    List<AssessmentSession> search(DeliverySearchParameters search) {
        List<AssessmentSession> foundSessions = new ArrayList<>()

        assessmentSessions.each{ k, v ->
            if(CollectionUtils.isNotEmpty(search.userIds)
                    && search.userIds.contains(v.userId)
                    && CollectionUtils.isNotEmpty(search.contentIdentifiers)
                    && search.contentIdentifiers.contains(v.contentIdentifier) ) {
                foundSessions.add(v)
            }
        }


        if(CollectionUtils.isNotEmpty(search.ids)
                || CollectionUtils.isNotEmpty(search.fields)
                || CollectionUtils.isNotEmpty(search.contentIds)
                || search.completionDateLowerBound != null
                || search.completionDateUpperBound != null
                || search.limit != null
                || search.skip != null
                || search.startDateLowerBound != null
                || search.startDateUpperBound != null ){
            System.out.println("These attributes have not be implemented for testing " + search);
            throw new NotImplementedException();
        }
        return foundSessions;
    }
}
