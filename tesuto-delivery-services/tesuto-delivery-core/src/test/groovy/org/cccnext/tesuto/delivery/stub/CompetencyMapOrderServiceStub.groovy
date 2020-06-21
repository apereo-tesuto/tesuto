package org.cccnext.tesuto.delivery.stub

import java.util.List
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySet
import org.cccnext.tesuto.content.dto.shared.SelectedOrderedCompetencies;
import org.cccnext.tesuto.content.service.CompetencyMapOrderService;

class CompetencyMapOrderServiceStub implements CompetencyMapOrderService {

    Map<String, String> disciplineMapsVerification = new HashMap<String, String>();
    @Override
    public String create(CompetencyMapDto mapDto) {
        
        return null;
    }

    @Override
    public Map<String, String> createForDisciplines(Set<String> disciplines) {
        for(String discipline:disciplines) {
            disciplineMapsVerification.put(discipline, discipline + "ID");
        }
        return null;
    }

    @Override
    public List<CompetencyDifficultyDto> getOrderedCompetencies(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CompetencyDifficultyDto> getOrderedCompetencies(String id, int version) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String findLatestPublishedIdByCompetencyMapDiscipline(String discipline) {
        // TODO Auto-generated method stub
        return disciplineMapsVerification.get(discipline);
    }

    @Override
    public Integer findPositionByAbility(String id, Double studentDificulty) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public OrderedCompetencySet selectOrganizeByAbility(
            String id, Double studentDificulty, Integer parentLevel, Integer competencyRange) {
        // TODO Auto-generated method stub
        return null;
    }
	
	@Override
	public String getCompetencyMapOrderId(String id) {
		return null;
	}
	
	

}
