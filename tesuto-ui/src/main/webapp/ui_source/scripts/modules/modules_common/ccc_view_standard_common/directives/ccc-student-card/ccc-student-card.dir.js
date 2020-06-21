/*-------------------------------------------------------------------------------
# Copyright © 2019 by California Community Colleges Chancellor's Office
# 
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License.  You may obtain a copy
# of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
# License for the specific language governing permissions and limitations under
# the License.
#------------------------------------------------------------------------------*/
(function () {

    angular.module('CCC.View.Common').directive('cccStudentCard', function () {

        return {

            restrict: 'E',

            scope: {
                student: '=?',
                issearch: '=?',
                showcard: '=?',
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    /*============ MODEL ============*/

                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [
                '<div class="user-data">',
                    '<h3 class="name">',
                        '<span class="value lastname emphasize">{{student.lastName}}</span>, ',
                        '<span class="value firstname">{{student.firstName}}</span> ',
                        '<span ng-if="student.middleInitial" class="value middlename">{{student.middleInitial}}.</span>',
                    '</h3>',
                    '<div class="profile-data">',
                        '<span class="data cccid"><span class="data-label" translate="CCC_VIEW_STANDARD_COMMON.CCC-STUDENT-CARD.CCCID"></span> <span class="value">{{student.cccId}}</span></span>',
                        '<span class="data age"><span class="data-label" translate="CCC_VIEW_STANDARD_COMMON.CCC-STUDENT-CARD.AGE"></span> <span class="value">{{student.age}}</span></span>',
                        '<span class="data phone"><span class="data-label" translate="CCC_VIEW_STANDARD_COMMON.CCC-STUDENT-CARD.PHONE"></span> <span class="value">{{student.phoneFormatted}}</span></span>',
                        '<span class="data email"><span class="data-label" translate="CCC_VIEW_STANDARD_COMMON.CCC-STUDENT-CARD.EMAIL"></span> <span class="value">{{student.email}}</span></span>',
                    '</div>',
                '</div>',
            ].join('')
        };
    });

})();
