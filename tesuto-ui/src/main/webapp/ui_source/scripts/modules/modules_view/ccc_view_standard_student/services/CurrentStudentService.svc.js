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

    angular.module('CCC.View.Student').provider('CurrentStudentService', function () {

        /*=========== SERVICE PROVIDER API ===========*/

        // current user reference and an outline of what we expect to come from the server
        var currentStudent = {
            cccId: null,
            displayName: null,
            colleges: [],
            collegeStatuses: {}
        };

        // the provider allows you to set the current user during the config phase
        // we get json data from the JSP and pass that in as the intial assessment data
        this.setStudent = function (student_in) {
            currentStudent = student_in;
        };


        /*============ SERVICE DECLARATION ============*/

        this.$get = [

            '$rootScope',
            '$q',
            'RoleService',

            function ($rootScope, $q, RoleService) {

                var CurrentStudentService;


                /*============ PRIVATE VARIABLES AND METHODS ============*/


                /*============ SERVICE API ============*/

                CurrentStudentService = {

                    /*============ PUBLIC PROPERTIES ============*/

                    /*============ PUBLIC METHODS ============*/

                    getStudent: function () {
                        return currentStudent;
                    },

                    getAvailableCollegeForPlacementIds: function () {

                        var collegesForPlacementIds = [];
                        _.forEach(currentStudent.collegeStatuses, function(value, key) {

                            // 1 = applied, 2 = enrolled
                            if (value === 1 || value === 2) {
                                collegesForPlacementIds.push(key);
                            }
                        });

                        return collegesForPlacementIds;
                    },

                    getAvailableCollegesForPlacement: function  () {

                        // Loop through the colleges on the student object and pull out the ones that match the collegeids
                        var currentCollegeStatus;
                        var collegesAvailableForPlacement = _.filter(currentStudent.colleges, function (college) {
                            currentCollegeStatus = currentStudent.collegeStatuses[college.cccId];
                            return currentCollegeStatus !== undefined && (currentCollegeStatus === 1 || currentCollegeStatus === 2);
                        });

                        return $q.when(collegesAvailableForPlacement);
                    }
                };


                /*=========== LISTENERS ===========*/

                /*============ INITIALIZATION ============*/

                RoleService.setRole(['STUDENT']);

                return CurrentStudentService;
            }
        ];
    });

})();
