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
package org.cccnext.tesuto.placement.lambda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.cccnext.tesuto.placement.model.CACompetencyMapDiscipline;
import org.cccnext.tesuto.placement.model.CAElaIndicator;
import org.cccnext.tesuto.placement.model.CAPlacement;
import org.cccnext.tesuto.placement.model.CAPlacementComponent;
import org.cccnext.tesuto.placement.model.CAPlacementComponentType;
import org.cccnext.tesuto.placement.model.CAPlacementCourse;
import org.cccnext.tesuto.placement.model.CAPlacementSubjectArea;
import org.cccnext.tesuto.placement.model.CAPlacementTransaction;
import org.cccnext.tesuto.placement.service.PlacementNotificationDto;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;

class CAPlacementAssembler {

	CAPlacementTransaction assemble(PlacementNotificationDto notification) {
		CAPlacementTransaction transaction = new CAPlacementTransaction();
		transaction.setMisCode(notification.getMisCode());
		transaction.setCccid(notification.getCccId());
		transaction.setEppn(notification.getEppn());
		if (notification.getPlacement() != null) {
			transaction.setSubjectArea(assembleSubjectArea(notification.getPlacement()));
		} else {
            transaction.setSubjectArea(assembleSubjectArea(notification.getPlacementComponents()));
		}
		return transaction;
	}

	CAPlacementSubjectArea assembleSubjectArea(Collection<PlacementComponentViewDto> components) {
		if (components.isEmpty()) {
			return null;
		} else {
			PlacementComponentViewDto first = components.iterator().next();
			VersionedSubjectAreaViewDto subjectAreaView = first.getVersionedSubjectAreaViewDto();
            return assembleSubjectArea(subjectAreaView, components);
		}
	}


	CAPlacementSubjectArea assembleSubjectArea(PlacementViewDto placement) {
        VersionedSubjectAreaViewDto subjectAreaView = placement.getVersionedSubjectAreaViewDto();
        CAPlacementSubjectArea subjectArea = assembleSubjectArea(subjectAreaView, placement.getPlacementComponents());
        subjectArea.setPlacements(Collections.singletonList(assemblePlacement(placement)));
		return subjectArea;
	}

	CAPlacementSubjectArea assembleSubjectArea(
	        VersionedSubjectAreaViewDto subjectAreaView,
            Collection<PlacementComponentViewDto> components) {
        CAPlacementSubjectArea subjectArea = new CAPlacementSubjectArea();
        CACompetencyMapDiscipline competencyMapDiscipline =
				assembleCompetencyMapDiscipline(subjectAreaView.getCompetencyMapDiscipline());
        subjectArea.setCompetencyMapDiscipline(competencyMapDiscipline);
        CompetencyAttributesViewDto attributes = subjectAreaView.getCompetencyAttributes();
        if (attributes != null) {
            subjectArea.setOptInMmap(attributes.isPlacementComponentMmap());
            subjectArea.setOptInMultipleMeasures(attributes.isOptInMultiMeasure());
            subjectArea.setOptInSelfReported(attributes.isUseSelfReportedDataForMM());
        }
        subjectArea.setSubjectAreaId(subjectAreaView.getDisciplineId());
        subjectArea.setSubjectAreaVersion(subjectAreaView.getVersion());
        subjectArea.setSisTestName(subjectAreaView.getSisCode());
        List<CAPlacementComponent> caComponents = convert(components, (c -> assemblePlacementComponent(c)));
        subjectArea.setPlacementComponents(caComponents);
        return subjectArea;
    }

	CAPlacement assemblePlacement(PlacementViewDto view) {
		CAPlacement placement = new CAPlacement();
		placement.setCb21(view.getCb21Code());
		placement.setCourseGroup(view.getCourseGroup().toString());
		placement.setPlacementDate(view.getCreatedOn());
		placement.setPlacementId(view.getId());
		placement.setElaIndicator(convertEla(view.getElaIndicator())); 
		placement.setIsAssigned(view.isAssigned());
		placement.setAssignedDate(view.getAssignedDate());
		placement.setPlacementComponentIds(convert(view.getPlacementComponents(), (c -> c.getId())));
		placement.setCourses(convert(view.getCourses(), (c -> assembleCourse(c))));
		return placement;
	}


	CAPlacementComponent assemblePlacementComponent(PlacementComponentViewDto view) {
		String entityClass = view.getEntityTargetClass();
		String tesutoClassName =	"org.cccnext.tesuto.placement.model.TesutoPlacementComponent";
		CAPlacementComponentType componentType =
			tesutoClassName.equals(entityClass) ?
			CAPlacementComponentType.CCCAssess :
			CAPlacementComponentType.Mmap;
		CAPlacementComponent component = new CAPlacementComponent(componentType);
		component.setCb21(view.getCb21Code());
		component.setCourseGroup(view.getCourseGroup().toString());
		component.setPlacementComponentDate(view.getCreatedOn());
		component.setPlacementComponentId(view.getId());
		component.setElaIndicator(convertEla(view.getElaIndicator()));
		component.setTrigger(view.getTriggerData());
		component.setCourses(convert(view.getCourses(), (c ->assembleCourse(c))));
		return component;
	}


	CAElaIndicator convertEla(String elaIndicator) {
		return "English".equalsIgnoreCase(elaIndicator) ? CAElaIndicator.English :
			"ESL".equalsIgnoreCase(elaIndicator) ? CAElaIndicator.ESL :
			"English".equalsIgnoreCase(elaIndicator) ? CAElaIndicator.English :
			CAElaIndicator.NA;
			
	}


	CAPlacementCourse assembleCourse(CourseViewDto view) {
		CAPlacementCourse course = new CAPlacementCourse();
		course.setSubject(view.getSubject());
		course.setNumber(view.getNumber());
		course.setSisTestMappingLevel(view.getSisTestCode());
		return course;
	}

	CACompetencyMapDiscipline assembleCompetencyMapDiscipline(String discipline) {
		CACompetencyMapDiscipline[] values = CACompetencyMapDiscipline.values();
		for (int i=0; i<values.length; ++i) {
			if (values[i].toString().equalsIgnoreCase(discipline)) {
				return values[i];
			}
		}
		return null;
	}

	<T,F> List<T> convert(Collection<F> things, Function<F,T> convert) {
		if (things == null) {
			return new ArrayList<T>();
		} else {
			return things.stream().map(t -> convert.apply(t)).collect(Collectors.toList());
		}
	}


	String capitalize(String word) {
		return word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
	}
}
