/*******************************************************************************
 * Copyright © 2019 by California Community Colleges Chancellor's Office
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.cccnext.tesuto.placement.service;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.placement.model.Placement;
import org.cccnext.tesuto.placement.model.PlacementComponentNotification;
import org.cccnext.tesuto.placement.model.PlacementNotification;
import org.cccnext.tesuto.placement.repository.jpa.PlacementComponentNotificationRepository;
import org.cccnext.tesuto.placement.repository.jpa.PlacementComponentRepository;
import org.cccnext.tesuto.placement.repository.jpa.PlacementNotificationRepository;
import org.cccnext.tesuto.placement.repository.jpa.PlacementRepository;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.service.StudentCollegeAffiliationReader;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class PlacementNotificationServiceImpl implements PlacementNotificationService {


    @Value("${placement.notification.topic}")
    private String topicArn;

    @Value("${placement.notification.send}")
    boolean sendNotification;

    ObjectMapper objectMapper = new ObjectMapper();

  	@Autowired
  	PlacementAssembler placementAssembler;

  	@Autowired
  	PlacementComponentAssembler placementComponentAssembler;

    @Autowired
    PlacementRepository placementRepository;

  	@Autowired
    PlacementComponentRepository placementComponentRepository;

    @Autowired
    PlacementComponentService placementComponentService;

    @Autowired
    PlacementServiceImpl placementService;

    @Autowired
    PlacementNotificationRepository placementNotificationRepository;

    @Autowired
    PlacementComponentNotificationRepository placementComponentNotificationRepository;

    StudentCollegeAffiliationReader studentCollegeAffiliationReader;

    private AmazonSNS snsClient;

    public AmazonSNS getSnsClient() {
        return snsClient;
    }

    public void setSnsClient(AmazonSNS snsClient) {
        this.snsClient = snsClient;
    }

    public String getTopicArn() {
        return topicArn;
    }

    public void setTopicArn(String topicArn) {
        this.topicArn = topicArn;
    }

    public boolean isSendNotification() {
        return this.sendNotification;
    }

    public void setSendNotification(boolean sendNotification) {
        this.sendNotification = sendNotification;
    }


    public StudentCollegeAffiliationReader getStudentCollegeAffiliationReader() {
        return this.studentCollegeAffiliationReader;
    }

    public void setStudentCollegeAffiliationReader(StudentCollegeAffiliationReader reader) {
        this.studentCollegeAffiliationReader = reader;
    }

    @PostConstruct
    public void initialize() {
        if (sendNotification) {
            snsClient = AmazonSNSClientBuilder.defaultClient();
        }
    }

    private void placementNotification(String placementId, Date time, Boolean success) {
        PlacementNotification notification = new PlacementNotification();
        notification.setPlacementId(placementId);
        notification.setNotificationSent(time);
        notification.setSuccess(success);
        placementNotificationRepository.save(notification);
    }

	private void placementComponentNotification(Set<String> componentIds, Date time, Boolean success) {
        for (String componentId : componentIds) {
            PlacementComponentNotification notification = new PlacementComponentNotification();
            notification.setPlacementComponentId(componentId);
            notification.setNotificationSent(time);
            notification.setSuccess(success);
            placementComponentNotificationRepository.save(notification);
        }
	}


	private String getEppn(String cccId, String misCode) {
		try {
			StudentCollegeAffiliationDto aff =
				studentCollegeAffiliationReader.findByCccIdAndMisCode(cccId, misCode);
			return aff.getEppn();
		} catch (Exception e) {
			return null; //we probably don't have a record for this student
		}
	}

    private void notify(PlacementNotificationDto notification) throws Exception {
        String message = objectMapper.writeValueAsString(notification);
        snsClient.publish(topicArn, message);
    }

    @Override
    @Transactional
    public void placementDecisionComplete(String placementId) {
        if (!sendNotification) {
            return;
        }
		Placement placement = placementRepository.getOneWithComponents(placementId);
		PlacementViewDto view = placementService.updatePlacement(placementAssembler.assembleDto(placement));
		Set<PlacementComponentViewDto> componentViews =
            placementComponentAssembler.assembleDto(placement.getPlacementComponents()).stream().
            map(comp -> placementComponentService.updateComponent(comp)).
            collect(Collectors.toSet());
		PlacementNotificationDto notification = new PlacementNotificationDto(placement.getCollegeId(), placement.getCccid());
		notification.setPlacement(view);
		notification.setPlacementComponents(componentViews);
		notification.setEppn(getEppn(placement.getCccid(), placement.getCollegeId()));
        try {
            notify(notification);
            Date now = new Date();
            placementNotification(view.getId(), now, true);
		} catch (Exception e) {
            log.error(e.getMessage(), e);
            Date now = new Date();
			placementNotification(view.getId(), now, false);
		}
    }

    @Override
    @Transactional
	public void placementComponents(String misCode, String cccId, String trackingId) {
        if (!sendNotification) {
            return;
        }
		PlacementNotificationDto notification = new PlacementNotificationDto(misCode, cccId);
		notification.setEppn(getEppn(cccId, misCode));
        Collection<PlacementComponentViewDto> components =
            placementComponentService.getPlacementComponentsByTrackingId(trackingId);
        if(CollectionUtils.isEmpty(components)) {
            PlacementComponentViewDto view = new PlacementComponentViewDto();
            view.setCccid(cccId);
            view.setCollegeId(misCode);
            view.setTrackingId(trackingId);
            view.setCreatedOn(new Date());
            components.add(view);
        }
		notification.setPlacementComponents(components);
		Set<String> componentIds = components.stream().map(c -> c.getId()).collect(Collectors.toSet());
		try {
			notify(notification);
            placementComponentNotification(componentIds, new Date(), true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			placementComponentNotification(componentIds, new Date(), false);
		}
    }
}
