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

    angular.module('CCC.View.Home').directive('cccAccommodationsList', function () {

        return {

            restrict: 'E',

            scope: {
                prepopulatedAccommodations: '=?',
                showAccommodations: '=',
                student: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$translate',
                'AccommodationsListService',

                function ($scope, $element, $timeout, $translate, AccommodationsListService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    /*============= MODEL =============*/

                    $scope.accommodationsLoadingComplete = false;
                    $scope.isOtherAccommodationsSelected = false;
                    $scope.hasErrors = false;

                    $scope.accommodationRadios = [];

                    $scope.chosenAccommodations = [];
                    $scope.otherAccommodations = '';


                    /*============= MODEL DEPENDENT METHODS =============*/

                    var createAccommodationRadios = function (accommodations) {

                        var accommodationList = accommodations;
                        $scope.accommodationRadios = [];

                        _.each(accommodationList, function (accommodation) {
                            var translatedName = $translate.instant('CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ACCOMMODATIONS.CODES.' + accommodation.code);
                            $scope.accommodationRadios.push({
                                name: 'accommodation',
                                title: accommodation.code + ' - ' + translatedName,
                                value: accommodation.code,
                                id: accommodation.id,
                                description: accommodation.description,
                                checked: false,
                                hidden: accommodation.code === 'N'
                            });
                        });

                    };

                    // Set a default hidden accommodation to "No special accommodation"
                    var setDefaultAccommodations = function () {
                        if ($scope.showAccommodations === false) {
                            $scope.chosenAccommodations = [];
                            $scope.otherAccommodations = '';

                            _.each($scope.accommodationRadios, function (accommodation) {
                                if (accommodation.value === 'N') {
                                    accommodation.checked = true;
                                } else {
                                    accommodation.checked = false;
                                    $element.find('input').attr("checked", false);
                                }
                            });

                            setSelectedAccommodations();
                        }
                    };

                    var setIsOtherAccommodationsSelected = function () {

                        for (var i=0; i < $scope.accommodationRadios.length; i++) {
                            if ($scope.accommodationRadios[i].value === "O" && $scope.accommodationRadios[i].checked) {
                                $scope.isOtherAccommodationsSelected = true;
                                return;
                            } else {
                                $scope.isOtherAccommodationsSelected = false;
                            }
                        }
                        if ($scope.isOtherAccommodationsSelected === false) {
                            $scope.otherAccommodations = '';
                        }
                    };

                    var setPrepopulatedAccommodations = function () {

                        var existingAccommodations = $scope.prepopulatedAccommodations.accommodations;

                        _.each(existingAccommodations, function (accommodation) {
                            accommodation.checked = true;
                        });

                        _.each($scope.accommodationRadios, function (radio) {
                            _.each(existingAccommodations, function (accommodation) {

                                // Tell the UI which accommodations should display as selected
                                if (radio.value === accommodation.code) {
                                    radio.checked = accommodation.checked;
                                }

                                // If no accommodations, then don't display accommodations
                                if (accommodation.code === 'N' && accommodation.checked) {
                                    $scope.showAccommodations = false;
                                } else {
                                    $scope.showAccommodations = true;
                                }

                                if (accommodation.code === 'O' && accommodation.checked) {
                                    $scope.isOtherAccommodationsSelected = true;
                                    $scope.otherAccommodations = $scope.prepopulatedAccommodations.accommodationsOther;
                                }
                            });
                        });

                        $scope.chosenAccommodations = existingAccommodations;
                    };

                    var setSelectedAccommodations = function () {

                        var selectedAccommodations = [];
                        var accommodationsToInclude = _.filter($scope.accommodationRadios, function (accommodation) {
                            return accommodation.checked;
                        });

                        if (accommodationsToInclude.length > 1) {
                            accommodationsToInclude = _.filter(accommodationsToInclude, function (accommodation) {
                                return accommodation.value !== 'N';
                            });
                        }

                        _.each(accommodationsToInclude, function (accommodation) {

                            selectedAccommodations.push({
                                name: accommodation.description,
                                id: accommodation.id,
                                code: accommodation.value
                            });
                        });

                        $scope.chosenAccommodations = selectedAccommodations;
                    };

                    var onAccommodationChange = function () {
                        setIsOtherAccommodationsSelected();
                        setSelectedAccommodations();
                        setDefaultAccommodations();
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.isAccommodationsOtherInvalid = function () {
                        return !$.trim($scope.otherAccommodations);
                    };


                    /*============= LISTENERS =============*/

                    $scope.$on('ccc-radio-box.change', function (event, radio, radioGroupId) {
                        if (radioGroupId === 'accommodations') {
                            onAccommodationChange();
                        }
                    });

                    $scope.$watch('showAccommodations', function () {
                        setDefaultAccommodations();
                        if ($scope.showAccommodations && $scope.otherAccommodations === '') {
                            $scope.isOtherAccommodationsSelected = false;
                        }
                    });

                    $scope.$on('ccc-activate-student.createActivation', function (e, student) {
                        if ($scope.student.cccId === student.cccId) {
                            $scope.$emit('ccc-accommodations-list.chosenAccommodations', $scope.chosenAccommodations, $scope.otherAccommodations);
                        }

                    });

                    $scope.$on('ccc-activation-edit.editActivation', function (e, student) {
                        if ($scope.student.cccId === student.cccId) {
                            $scope.$emit('ccc-accommodations-list.chosenAccommodations', $scope.chosenAccommodations, $scope.otherAccommodations);
                        }

                    });

                    $scope.$on('ccc-activate-student.accommodationsError', function () {
                        $scope.hasErrors = true;
                    });

                    $scope.$on('ccc-activation-edit.accommodationsError', function () {
                        $scope.hasErrors = true;
                    });

                    $scope.$watch('otherAccommodations', function () {
                        if ($scope.otherAccommodations !== '') {
                            $scope.hasErrors = false;
                        }
                    });


                    /*============ INITIALIZATION ============*/

                    AccommodationsListService.getAccommodationsList().then(function (accommodations) {

                        createAccommodationRadios(accommodations);

                        if ($scope.prepopulatedAccommodations) {

                            setPrepopulatedAccommodations();
                        } else {

                            setDefaultAccommodations();
                        }

                    }, function (err) {

                        createAccommodationRadios([]);

                    }).finally(function () {
                        $scope.accommodationsLoadingComplete = true;
                    });

                }
            ],

            template: [

                '<ccc-content-loading-placeholder ng-hide="accommodationRadios.length > 0" no-results="accommodationsLoadingComplete && accommodationRadios.length == 0"></ccc-content-loading-placeholder>',
                '<fieldset>',
                    '<legend class="sr-only">accomodation details</legend>',
                    '<ccc-radio-box class="ccc-radio-box-group-thin" input-type="checkbox" ng-repeat="radio in accommodationRadios track by radio.value" radio="radio" is-disabled="processing" radio-group-id="accommodations"></ccc-radio-box>',
                '</fieldset>',
                '<div ng-show="isOtherAccommodationsSelected && showAccommodations">',
                        '<label for="otherTextArea" class="col-sm-3 control-label sr-only">Accommodation Details</label>',
                        '<div ccc-show-errors="hasErrors">',
                            '<textarea id="otherTextArea" class="form-control resize-vertical" ng-model="otherAccommodations" ng-disabled="processing" ng-class="{\'error\': hasErrors}"></textarea>',
                            '<div ng-show="isAccommodationsOtherInvalid()" class="ccc-validation-messages noanim ccc-validation-messages-accommodationsOther">',
                                '<p>Other details are required.</p>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
