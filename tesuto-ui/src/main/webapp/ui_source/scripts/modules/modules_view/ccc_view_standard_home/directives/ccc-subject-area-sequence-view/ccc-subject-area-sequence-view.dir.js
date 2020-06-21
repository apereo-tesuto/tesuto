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

    angular.module('CCC.View.Home').directive('cccSubjectAreaSequenceView', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '='
            },

            controller: [

                '$scope',

                function ($scope) {


                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.done = function () {
                        $scope.$emit('ccc-subject-area-sequence-view.done');
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-subject-area-sequence.sequenceUpdated', function () {
                        $scope.$emit('ccc-subject-area-sequence-view.sequenceUpdated');
                    });

                }
            ],

            template: [

                '<h2 class="no-outline" ccc-focusable ccc-autofocus>',
                    '<i class="fa fa-list-ul" aria-hidden="true"></i> ',
                    '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.HEADER"></span>',
                '</h2>',

                '<ccc-subject-area-sequence subject-area="subjectArea"></ccc-subject-area-sequence>',

                '<div>',
                    '<button class="btn btn-primary btn-submit-button btn-icon-left" ng-click="done()"><i class="fa fa-chevron-left"></i> Done</button>',
                '</div>'

            ].join('')

        };

    });

})();
