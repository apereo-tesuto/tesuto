package org.cccnext.tesuto.placement.service;

import javax.validation.constraints.NotNull;

import org.cccnext.tesuto.placement.model.CAPlacementTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for performing college adaptor operations
 *
 * @author zrogers
 */
@Service
public class CollegeAdaptorService {

    private static final Logger logger = LoggerFactory.getLogger(CollegeAdaptorService.class);

    @Autowired
    public CollegeAdaptorService() {
    }



    /**
     * Add a Placement Transaction for a student
     *
     * @param misCode                MIS Code
     * @param caPlacementTransaction Placement Transaction
     * @throws CollegeAdaptorException College Adaptor Exception
     */
    public void addPlacementTransaction(@NotNull String misCode, @NotNull CAPlacementTransaction caPlacementTransaction)
	{

	}
}