/**
 * Created by bruce on 1/11/17.
 */

package org.cccnext.tesuto.service.scoring

import org.cccnext.tesuto.delivery.model.internal.TaskSet


class TaskSetDaoStub implements org.cccnext.tesuto.delivery.service.TaskSetDao {

    Map<String,TaskSet> taskSets = [:]


    @Override
    String create(TaskSet taskSet) {
        taskSets[taskSet.taskSetId] = taskSet
        return taskSet.taskSetId
    }

    @Override
    boolean delete(String id) {
        taskSets.remove(id)
    }

    @Override
    TaskSet doFind(String id) {
        return taskSets[id]
    }

    @Override
    TaskSet save(TaskSet taskSet) {
        return taskSets[taskSet.taskSetId] = taskSet
    }
}
