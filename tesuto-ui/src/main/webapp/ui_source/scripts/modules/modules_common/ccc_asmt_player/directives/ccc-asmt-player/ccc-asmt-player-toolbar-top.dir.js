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

    // https://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#section10092

    angular.module('CCC.AsmtPlayer').directive('cccAsmtPlayerToolbarTop', function () {

        return {

            restrict: 'E',

            scope: {
                assessment: "=",
                user: "="
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'AsmtService',
                'BackgroundContentService',
                'CalculatorToolService',
                'ToolsPermissionService',

                function ($scope, $element, $timeout, AsmtService, BackgroundContentService, CalculatorToolService, ToolsPermissionService) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/


                    /*=========== MODEL ===========*/

                    $scope.showTools = false;
                    $scope.zoomLevel = 1; // todo: zoom level and other student settings should be in a service

                    $scope.asmtState = AsmtService.state;

                    $scope.calculatorState = CalculatorToolService.state;

                    $scope.permissionService = ToolsPermissionService;


                    /*=========== BEHAVIOR ===========*/

                    $scope.toggleCalculator = function () {
                        CalculatorToolService.toggle();
                    };

                    $scope.toggleTools = function () {
                        $scope.showTools = !$scope.showTools;

                        // if we are showing now, focus on the first button
                        if ($scope.showTools) {

                            // give it just a quick second so it can render
                            $timeout(function () {
                                $($element.find('.ccc-asmt-player-tools button')[0]).focus();
                            }, 1);
                        }
                    };

                    $scope.toggleInvert = function () {
                        $('body').toggleClass('ccc-invert');
                    };

                    $scope.isZoomSelected = function (zoomLevel) {
                        return {
                            active: $scope.zoomLevel === zoomLevel
                        };
                    };

                    $scope.selectZoomLevel = function (zoomLevel) {
                        $('body').removeClass('ccc-zoom-' + $scope.zoomLevel);
                        $('body').addClass('ccc-zoom-' + zoomLevel);
                        $scope.zoomLevel = zoomLevel;
                    };

                    $scope.backgroundClass = function (backgroundContentId) {
                        return {
                            active: BackgroundContentService.getBackgroundContentId() === backgroundContentId
                        };
                    };

                    $scope.openSettings = function () {

                        BackgroundContentService.openBackgroundContent({
                            id: 'assessment-navigation',
                            title: 'CCC_PLAYER.NAVIGATION.HEADER',
                            position: 'left',
                            template: '<ccc-asmt-player-navigation></ccc-asmt-player-navigation>',
                            width: 40,
                            onClose: function () {
                                $element.find('.ccc-asmt-open-navigation-button').focus();
                            }
                        });
                    };

                    $scope.openStudentOptions = function () {

                        BackgroundContentService.openBackgroundContent({
                            id: 'student-options',
                            title: 'CCC_PLAYER.STUDENT_OPTIONS.HEADER',
                            position: 'left',
                            template: '<ccc-asmt-player-student-options></ccc-asmt-player-student-options>',
                            width: 40,
                            onClose: function () {
                                $element.find('.ccc-asmt-open-student-options-button').focus();
                            }
                        });
                    };

                    $scope.exit = function () {

                    };


                    /*=========== LISTENERS ===========*/

                    /*=========== INITIALIZATION ===========*/

                }
            ],

            template: [
                '<div class="ccc-asmt-player-toolbar">',
                    '<div class="container">',
                        '<div class="row">',

                            '<div class="col-xs-9 text-left">',

                                //'<button ng-if="assessment.navigationMode === \'NONLINEAR\'" ng-click="openNavigation()" class="btn btn-default btn-icon-only ccc-asmt-open-navigation-button pull-left" ng-class="backgroundClass(\'assessment-navigation\')" role="button" tabindex="0"><i class="fa fa-list"></i> <span class="sr-only">Assessment Navigation Menu</span></button>',

                                '<div ccc-dropdown-focus class="dropdown pull-left ccc-user-options-menu">',
                                    '<button class="btn btn-default dropdown-toggle app-nav-button ccc-asmt-open-student-options-button" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">',
                                        '<span class="hidden-xs">{{::user.displayName}}</span>',
                                        '<span class="visible-xs-* hidden-sm hidden-md hidden-lg"><i class="fa fa-user" aria-hidden="true"></i></span>',
                                        '<span class="sr-only" translate="CCC_PLAYER.CONTROLS.ASSESSMENT_OPTIONS.BUTTON"></span>',
                                        '<span class="caret"></span>',
                                    '</button>',
                                    '<ul class="dropdown-menu" aria-labelledby="dropdownMenu1">',
                                        '<li>',
                                            '<a href="#" id="ccc-header-nav-settings" ng-click="openStudentOptions()" ng-class="backgroundClass(\'student-options\')">',
                                                '<i class="fa fa-cog" aria-hidden="true"></i> ',
                                                '<span translate="CCC_PLAYER.CONTROLS.ASSESSMENT_OPTIONS.SETTINGS"></span>',
                                            '</a>',
                                        '</li>',
                                        //'<li role="separator" class="divider"></li>',
                                        // '<li>',
                                        //     '<a href="#" id="ccc-header-nav-sign-out" ng-click="exit()">',
                                        //         '<i class="fa fa-sign-out" aria-hidden="true"></i> ',
                                        //         '<span translate="CCC_PLAYER.CONTROLS.ASSESSMENT_OPTIONS.EXIT"></span>',
                                        //     '</a>',
                                        // '</li>',
                                    '</ul>',
                                '</div>',

                                '<h1 class="ccc-asmt-player-asmt-title-container pull-left">',
                                    '<span class="ccc-asmt-player-asmt-title">{{assessment.title}} </span>',
                                    '<span ng-if="assessment.navigationMode === \'NONLINEAR\' && asmtState.totalTaskCount">({{asmtState.totalTaskIndex + 1}} of {{asmtState.totalTaskCount}})</span>',
                                '</h1>',

                            '</div>',

                            '<div class="col-xs-3 text-right">',
                                '<div class="btn-group">',
                                    //'<span class="btn btn-default ccc-asmt-player-tool btn-icon-only" role="button"><span class="hidden-md hidden-xs hidden-sm" translate="CCC_PLAYER.TOOLS.DICTIONARY"></span> <i class="fa fa-book" aria-hidden="true"></i></span>',
                                    '<span class="btn btn-default ccc-asmt-player-tool btn-icon-only" id="ccc-calculator" role="button" ng-click="toggleCalculator()" ng-class="{active:calculatorState.isOpen}" ng-if="permissionService.getPermissions().allowCalculator"><span class="hidden-xs" translate="CCC_PLAYER.TOOLS.CALCULATOR"></span> <i class="fa fa-calculator" aria-hidden="true"></i></span>',
                                '</div>',
                            '</div>',

                        '</div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
