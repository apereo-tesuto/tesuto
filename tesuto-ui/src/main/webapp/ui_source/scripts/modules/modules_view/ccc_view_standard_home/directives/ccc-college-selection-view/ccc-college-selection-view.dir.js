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

    angular.module('CCC.View.Home').directive('cccCollegeSelectionView', function () {

        return {

            restrict: 'E',

            scope: {
                initialCccId: '=?'
            },

            controller: [

                '$scope',
                '$element',

                function ($scope, $element) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    var setFocusedElement = function (element) {
                        $element.find('[ccc-autofocus]').removeAttr('ccc-autofocus');
                        $(element).attr('ccc-autofocus', true);
                    };


                    /*============ MODEL ==============*/

                    $scope.loading = true;


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-colleges-list.collegesLoaded', function (e, colleges) {

                        $scope.loading = false;
                        $scope.$emit('ccc-college-selection-view.collegesLoaded', colleges);
                    });

                    $scope.$on('ccc-colleges-list.selected', function (e, college, collegeElement) {

                        setFocusedElement(collegeElement);
                        $scope.$emit('ccc-college-selection-view.collegeSelected', college);
                    });


                    /*============ INITIALIZATION ==============*/
                }
            ],

            template: [

                '<div class="row" ng-if="loading">',
                    '<div class="col-md-8 col-md-offset-2">',

                        '<ccc-content-loading-placeholder class="ccc-college-selection-view-loader"></ccc-content-loading-placeholder>',

                    '</div>',
                '</div>',

                // we just hide it so the college list actually compiles so we can listen to its events
                '<div class="row" ng-hide="loading">',
                    '<div class="col-md-8 col-md-offset-2">',

                        '<div class="row margin-bottom">',

                            '<div class="col-xs-12">',
                                '<h2 tabindex="0" ccc-autofocus class="no-outline" id="select-college-header">Select a College</h2>',
                            '</div>',

                        '</div>',

                        '<div class="row">',
                            '<div class="col-md-12">',
                                '<ccc-colleges-list college-ccc-id="initialCccId" described-by="select-college-header"></ccc-colleges-list>',
                            '</div>',
                        '</div>',

                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
