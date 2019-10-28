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

    angular.module('CCC.View.Home').service('StudentPlacementService', [

        '$q',
        '$timeout',
        'PlacementCollegesAPIService',

        function ($q, $timeout, PlacementCollegesAPIService) {

            /*============ SERVICE DECLARATION ============*/

            var StudentPlacementService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            StudentPlacementService = {};

            // this method will do a little post processing to insert component references into each of the placements
            StudentPlacementService.getPlacementDataForStudentByCollege = function (studentId, collegeId) {

                var placementDeferred = $q.defer();
                var placements = null;
                var components = null;

                var processPlacementData = function (placementType, placementData) {

                    if (placementType === 'placements') {

                        placements = placementData;

                    } else if (placementType === 'components') {

                        components = placementData;
                    }

                    if (placements !== null && components !== null) {

                        _.each(placements, function (placement) {

                            if (placement.placementComponentIds === null) {
                                throw new Error('StudentPlacementService.placementComponentIds.null.' + placement.id);
                            }

                            placement.componentObjects = _.filter(components, function (component) {
                                return placement.placementComponentIds.indexOf(component.id) !== -1;
                            });
                        });

                        placementDeferred.resolve({
                            placements: placements,
                            components: components
                        });
                    }
                };

                PlacementCollegesAPIService.getPlacementsForStudentByCollege(studentId, collegeId).then(function (results) {

                    processPlacementData('placements', results);

                }, function (err) {

                    if (err.status + '' === '404') {
                        processPlacementData('placements', []);
                    } else {
                        placementDeferred.reject(err);
                    }
                });

                PlacementCollegesAPIService.getPlacementComponentsForStudentByCollege(studentId, collegeId).then(function (results) {

                    processPlacementData('components', results);

                }, function (err) {

                    if (err.status + '' === '404') {
                        processPlacementData('components', []);
                    } else {
                        placementDeferred.reject(err);
                    }
                });

                return placementDeferred.promise;
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return StudentPlacementService;

        }
    ]);

})();
