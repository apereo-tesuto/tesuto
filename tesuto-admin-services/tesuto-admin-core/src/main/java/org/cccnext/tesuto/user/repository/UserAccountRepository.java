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
package org.cccnext.tesuto.user.repository;

import org.cccnext.tesuto.user.model.UserAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Set;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface UserAccountRepository extends PagingAndSortingRepository<UserAccount, String> {
    // Query Cache
    /* Commented out as part of CCCAS-5452. Replaced by findAllNotDelted.
    If this needs to be uncommented for some reason, keep in mind that this will return
    deleted users as well as not-deleted users.
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheRegion", value = "userAccount"),
            @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    public List<UserAccount> findAll(); */

    // Query Cache
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheRegion", value = "userAccount"),
            @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Query("select u from UserAccount u where u.deleted = false")
    List<UserAccount> findAllNotDeleted();

    public default UserAccount getOne(String userAccountId) {
    	return findById(userAccountId).get();
    }

    public UserAccount findByUserAccountId(String userAccountId);

    public UserAccount findByUsernameIgnoreCase(String username);

    @Query("select u from UserAccount u left join fetch u.userAccountColleges uc left join fetch uc.college where u.userAccountId = ?1")
    UserAccount findWithColleges(String userId);

    @Query("select u from UserAccount u inner join u.userAccountColleges uc where uc.collegeId = ?1 and u.deleted = false")
    Set<UserAccount> findByCollegeId(String collegeId);

    @Query("select u from UserAccount u inner join u.testLocations tl where tl.id = ?1 and u.deleted = false")
    Set<UserAccount> findByTestLocationId(String testLocationId);

    // @Query("select u from UserAccount u where u.userAccountId in ?1")
    List<UserAccount> findByUserAccountIdIn(List<String> userAccountIds);
}
