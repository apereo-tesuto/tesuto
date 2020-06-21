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

    angular.module('CCC.AsmtPlayer').directive('cccAsmtPlayerStudentOptions', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'MathService',
                'CurrentUserService',

                function ($scope, MathService, CurrentUserService) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    var selectMathRendering = function (isUsingMathML_in) {

                        var currentRenderMethod = MathService.getRenderer();
                        var renderMethod = isUsingMathML_in ? 'NativeMML' : 'HTML-CSS';

                        if (currentRenderMethod !== renderMethod) {
                            MathService.setRenderer(renderMethod);
                            $scope.$emit('ccc-asmt-player-student-options.mathRenderMethodUpdated', renderMethod);
                        }
                    };


                    /*=========== MODEL ===========*/

                    $scope.zoomLevel = CurrentUserService.getProperty('zoomLevel', 1);
                    $scope.renderMethod = MathService.getRenderer();
                    $scope.isUsingMathML = $scope.renderMethod === 'NativeMML';


                    /*=========== BEHAVIOR ===========*/

                    $scope.toggleInvert = function () {
                        $('body').toggleClass('ccc-invert');
                    };

                    $scope.zoomClass = function (zoomLevel) {
                        return {
                            active: $scope.zoomLevel === zoomLevel
                        };
                    };

                    $scope.selectZoomLevel = function (zoomLevel) {

                        var possibleZoomLevels = [1,2,3,4];
                        _.each(possibleZoomLevels, function (zoomLevel) {
                            $('body').removeClass('ccc-zoom-' + zoomLevel);
                        });

                        $('body').addClass('ccc-zoom-' + zoomLevel);
                        $scope.zoomLevel = zoomLevel;

                        CurrentUserService.setProperty('zoomLevel', $scope.zoomLevel);
                        $scope.$emit('ccc-asmt-player-student-options.baseFontSizeUpdated', $scope.zoomLevel);

                        // once the CSS animation is complete we can re-render the math
                        setTimeout(MathService.rerender, 1000);
                    };


                    /*=========== LISTENERS ===========*/

                    $scope.$watch('isUsingMathML', function (newValue, oldValue) {
                        if (newValue !== oldValue) {
                            selectMathRendering(newValue);
                        }
                    });


                    /*=========== INITIALIZATION ===========*/

                    $scope.$emit('ccc-asmt-player-student-options.open');

                }
            ],

            template: [
                '<div class="container-fluid">',
                    '<div class="row">',
                        '<div class="col-xs-12">',

                            '<div class="ccc-form-section">',

                                '<h4 class="ccc-form-section-title"><i class="fa fa-search" aria-hidden="true"></i> <span translate="CCC_PLAYER.STUDENT_OPTIONS.ZOOM.HEADER"></span></h4>',

                                '<div class="ccc-form-section-content">',
                                    '<label for="zoom-level-control">Set Zoom Level</label>',
                                    '<div class="btn-group" role="buttongroup" id="zoom-level-control">',
                                        '<a href="javascript://" class="btn btn-default" role="button" tabindex="0" ng-class="zoomClass(1)" ng-click="selectZoomLevel(1)"><span class="sr-only">zoom level</span> 1</a>',
                                        '<a href="javascript://" class="btn btn-default" role="button" tabindex="0" ng-class="zoomClass(2)" ng-click="selectZoomLevel(2)"><span class="sr-only">zoom level</span> 2</a>',
                                        '<a href="javascript://" class="btn btn-default" role="button" tabindex="0" ng-class="zoomClass(3)" ng-click="selectZoomLevel(3)"><span class="sr-only">zoom level</span> 3</a>',
                                        '<a href="javascript://" class="btn btn-default" role="button" tabindex="0" ng-class="zoomClass(4)" ng-click="selectZoomLevel(4)"><span class="sr-only">zoom level</span> 4</a>',
                                    '</div>',
                                '</div>',

                            '</div>',

                            // this may be put back after pilot, inversion of colors is not needed for pilot but the functionality does work
                            // '<div class="ccc-form-section">',
                            //     '<h4 class="ccc-form-section-title"><i class="fa fa-tint" aria-hidden="true"></i> <span translate="CCC_PLAYER.STUDENT_OPTIONS.COLOR.HEADER"></span></h4>',
                            //     '<div class="ccc-form-section-content">',
                            //         '<div>',
                            //             '<button class="btn btn-default" ng-click="toggleInvert()">Invert Colors</button>',
                            //         '</div>',
                            //     '</div>',
                            // '</div>',

                            //'<ccc-options-select-language></ccc-options-select-language>',

                            '<div class="ccc-form-section">',
                                '<h4 class="ccc-form-section-title"><i class="fa fa-calculator" aria-hidden="true"></i> <span translate="CCC_PLAYER.STUDENT_OPTIONS.MATH.HEADER"></span></h4>',
                                '<div class="ccc-form-section-content">',
                                    '<label translate="CCC_PLAYER.STUDENT_OPTIONS.MATH.RENDERER.TITLE" for="student-options-math-rendering"></label> ',
                                    '<ccc-toggle-input id="student-options-math-rendering" type="checkbox" toggle-value="isUsingMathML"></ccc-toggle-input>',
                                    '<div translate="CCC_PLAYER.STUDENT_OPTIONS.MATH.RENDERER.DESCRIPTION"></div>',
                                '</div>',
                            '</div>',

                        '</div>',
                    '</div>',
                '</div>'
            ].join('')

        };

    });

})();
