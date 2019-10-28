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

    angular.module('CCC.View.Home').directive('cccSubjectAreaPublish', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.publishSelectValue = null;

                    $scope.submitted = false;

                    $scope.publishComplete = false;

                    $scope.loading = false;

                    $scope.publishOptions = [
                        {
                            value: true,
                            label: 'CCC_VIEW_HOME.CCC-SUBJECT-AREA-PUBLISH.PUBLISH_CHECKBOX'
                        }
                    ];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var publishSubjectArea = function () {

                        $scope.loading = true;

                        $scope.subjectArea.publish().then(function (newSubjectArea) {

                            $scope.publishComplete = true;
                            $scope.$emit('ccc-subject-area-publish.subjectAreaPublished', newSubjectArea);

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };

                    var doSubmit = function () {

                        $scope.submitted = true;

                        if ($scope['publishForm'].$invalid) {
                            $($element.find('input.ng-invalid')[0]).focus();
                            return;
                        }

                        publishSubjectArea();
                    };




                    /*============ BEHAVIOR ==============*/

                    // we wrap in a timeout because when a user hit's "enter"
                    // the model values may not have propagated on each model from it's view value
                    // this let's the propagation take place
                    // hitting enter on an input within a form forces a click on the submit button which is the root cause
                    $scope.attemptDoSubmit = function () {
                        $timeout(doSubmit, 1);
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-subject-area-publish.cancel');
                    };

                    $scope.done = function () {
                        $scope.$emit('ccc-subject-area-publish.done');
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-detailed-select.changed', function (event, selectId, selectedOptions, options) {

                        if (selectedOptions.length) {
                            $scope.publishSelectValue = true;
                        } else {
                            $scope.publishSelectValue = null;
                        }

                        $scope.publishForm.publishSelect.$setDirty();
                    });


                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [

                '<div class="form-container">',

                    '<ng-form id="saInitialPublishForm row" name="publishForm">',

                        '<div class="form-body">',

                            '<div class="row">',
                                '<div class="col-md-offset-3 col-md-6">',

                                    '<div ng-if="!publishComplete">',

                                        '<h2 class="title" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-PUBLISH.TITLE"></h2>',

                                        '<div class="alert alert-warning alert-with-icon">',
                                            '<span class="icon fa fa-warning" role="presentation" aria-hidden="true"></span>',
                                            '<p class="alert-body" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-PUBLISH.WARNING_IMMEDIATE_EFFECTS"></p>',
                                        '</div>',

                                        '<div class="alert alert-warning alert-with-icon">',
                                            '<span class="icon fa fa-warning" role="presentation" aria-hidden="true"></span>',
                                            '<p class="alert-body" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-PUBLISH.WARNING_CANNOT_UNPUBLISH"></p>',
                                        '</div>',

                                        '<div class="ccc-form-container margin-top-lg">',

                                            '<fieldset ng-class="{\'ccc-show-errors\': submitted || publishForm.publishSelect.$dirty}">',

                                                '<legend class="sr-only" id="publishSelectLabel" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-COMPONENTS-LOGIC.TITLE_PLACEMENT"></legend>',

                                                '<ccc-detailed-select id="publishSelect" options="publishOptions" multi-select="true" is-required="true" is-disabled="loading" described-by="publishErrors" is-invalid="publishForm.publishSelect.$invalid"></ccc-detailed-select>',

                                                '<input class="hidden" name="publishSelect" required ng-model="publishSelectValue" ng-model-options="{allowInvalid: true}" autocomplete="off"/>',
                                                '<div id="publishErrors" class="ccc-validation-messages ccc-validation-messages-standalone noanim" ng-messages="publishForm.publishSelect.$error">',
                                                    '<p ng-message="required" class="noanim"><i class="fa fa-exclamation-triangle color-warning"></i> <span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-PUBLISH.REQUIRED"></span></p>',
                                                '</div>',

                                            '</fieldset>',

                                        '</div>',
                                    '</div>',

                                    '<div ng-if="publishComplete">',
                                        '<h2 class="title" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-PUBLISH.TITLE"></h2>',
                                        '<div class="alert alert-success alert-with-icon">',
                                            '<span class="icon fa fa-check-circle" role="presentation" aria-hidden="true"></span>',
                                            '<p class="alert-body" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-PUBLISH.SUCCESS"></p>',
                                        '</div>',
                                    '</div>',

                                '</div>',
                            '</div>',

                        '</div>',

                        '<div class="form-actions">',
                            '<div class="actions">',

                                '<div class="row">',
                                    '<div class="col-md-offset-3 col-md-6">',

                                        '<div ng-if="!publishComplete">',
                                            '<button class="btn btn-success" type="submit" ng-click="attemptDoSubmit()" ng-disabled="loading">',
                                                '<span class="icon fa fa-check-circle noanim" role="presentation" aria-hidden="true" ng-if="!loading"></span>',
                                                '<span class="icon fa fa-spin fa-spinner" role="presentation" aria-hidden="true" ng-if="loading"></span>',
                                                ' <span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-PUBLISH.ACTION_PUBLISH"></span>',
                                            '</button>',
                                            '<button class="btn btn-default cancel" ng-click="cancel()" ng-disabled="loading" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-PUBLISH.ACTION_CANCEL"></button>',
                                        '</div>',

                                        '<div ng-if="publishComplete">',
                                            '<button class="btn btn-success btn-submit-button" ng-click="done()"><span class="icon fa fa-check" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-PUBLISH.ACTION_DONE"></span></button>',
                                        '</div>',

                                    '</div>',
                                '</div>',

                            '</div>',
                        '</div>',

                    '</ng-form>',

                '</div>'

            ].join('')

        };

    });

})();
