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

    angular.module('CCC.View.Home').directive('cccAssessmentItemScoreInteractionChoice', function () {

        return {

            restrict: 'E',

            scope: {
                responseObject: '='
            },

            controller: [

                '$scope',
                '$element',
                '$compile',

                function ($scope, $element, $compile) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    /*=========== MODEL ===========*/

                    $scope.getRadioTextByIndex = function (index) {
                        return String.fromCharCode(97 + index).toUpperCase();
                    };

                    // CONTRACT: every interaction should end up with a set of values
                    $scope.itemClicked = function (event, choice) {

                        var indexOfChoiceSelected = $scope.responseObject.values.indexOf(choice.identifier);

                        if (indexOfChoiceSelected !== -1) {
                            $scope.responseObject.values.splice(indexOfChoiceSelected, 1);
                        } else {
                            $scope.responseObject.values.push(choice.identifier);
                        }
                    };

                    $scope.isChoiceSelected = function (choice) {
                        return $scope.responseObject.values.indexOf(choice.identifier) !== -1;
                    };


                    /*=========== LISTENERS ===========*/

                    /*=========== INITIALIZATION ===========*/
                }
            ],

            template: [

                '<span ng-repeat="choice in responseObject.interaction.choices track by $index" ng-click="itemClicked($event, choice)">',
                    '<ccc-choice-input is-selected="isChoiceSelected(choice)" text="getRadioTextByIndex($index)" type="type"></ccc-choice-input>',
                '</span>'

            ].join('')

        };

    });

})();
