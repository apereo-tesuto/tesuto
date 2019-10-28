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

    angular.module('CCC.View.Home').directive('cccWizardSummary', function () {

        return {

            restrict: 'E',

            scope: {
                wizardInstance: '=',    // pass in an instance of WizardClass
                ignoreFields: '=?'
            },

            controller: [

                '$scope',
                '$element',

                function ($scope, $element) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/


                    /*============= MODEL =============*/

                    $scope.summaryValues = [];

                    $scope.ignoreFields = $scope.ignoreFields || {};


                    /*============= MODEL DEPENDENT METHODS =============*/

                    var getFieldValue = function (fieldObj, fieldObjKey) {

                        return function () {

                            var fieldValue = $scope.wizardInstance.getModelValue(fieldObjKey);

                            if (angular.isArray(fieldValue)) {

                                var fieldValueArray = [];

                                _.each(fieldValue, function (fieldValueItem) {

                                    if (fieldObj.valueMapper) {
                                        fieldValueItem = fieldObj.valueMapper(fieldValueItem);
                                    }

                                    fieldValueArray.push(fieldValueItem);
                                });

                                fieldValue = fieldValueArray.join(', ');

                            } else {

                                if (fieldObj.valueMapper) {
                                    fieldValue = fieldObj.valueMapper(fieldValue);
                                }
                            }

                            return fieldValue;
                        };
                    };

                    var getFieldLabel = function (fieldLabelConfig, wizardInstance) {
                        if (_.isFunction(fieldLabelConfig)) {
                            return fieldLabelConfig(wizardInstance.model);
                        } else {
                            return fieldLabelConfig;
                        }
                    };

                    var setupSummaryModel = function () {

                        var requiredSteps = $scope.wizardInstance.getRequiredSteps();

                        $scope.summaryValues = [];

                        _.each(requiredSteps, function (stepWrapper) {

                            _.each(stepWrapper.step.fields, function (fieldObj, fieldObjKey) {

                                if (!fieldObj.hideInSummary && !$scope.ignoreFields[fieldObjKey]) {

                                    $scope.summaryValues.push({
                                        label: getFieldLabel(fieldObj.fieldLabel, $scope.wizardInstance),
                                        isReady: function () {
                                            return !stepWrapper.step.requiresValueMapper || stepWrapper.step.fields[fieldObjKey].valueMapper;   // the value mapper may end up being loaded dynamically so check for the requiresValueMapper property to show a spinner
                                        },
                                        getFieldValue: getFieldValue(fieldObj, fieldObjKey) // use a function because the valueMapper may be loaded dynamically
                                    });
                                }
                            });
                        });
                    };


                    /*============= BEHAVIOR =============*/

                    /*============= LISTENERS =============*/


                    /*============= INITIALIZATION ===========*/

                    setupSummaryModel();
                }
            ],

            template: [

                '<table class="table table-responsive table-striped">',
                    '<thead>',
                        '<tr>',
                            '<th translate="CCC_VIEW_HOME.CCC-WIZARD-SUMMARY.CONFIGURATION"></th>',
                            '<th translate="CCC_VIEW_HOME.CCC-WIZARD-SUMMARY.VALUE"></th>',
                        '</tr>',
                    '</thead>',
                    '<tbody>',
                        '<tr ng-repeat="summaryValue in summaryValues track by summaryValue.label">',
                            '<td width="30%">{{::summaryValue.label}}</td>',
                            '<td>',
                                '<span ng-if="summaryValue.isReady()" class="noanim">{{summaryValue.getFieldValue()}}</span>',
                                '<span ng-if="!summaryValue.isReady()" class="noanim"><i class="fa fa-spin fa-spinner" aria-hidden="true"></i><span class="sr-only">loading value</span></span>',
                            '</td>',
                        '</tr>',
                    '</tbody>',
                '</table>'

            ].join('')

        };

    });

})();
