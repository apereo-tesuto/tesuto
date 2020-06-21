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

    angular.module('CCC.Components').directive('cccViewManagerStaticView', function () {

        return {

            restrict: 'E',

            scope: {
                pageTitle: '@'
            },

            transclude: true,

            link: function(scope, element, attrs, ctrl, transclude) {
                transclude(scope, function(clone) {
                    scope.transcludeElement = clone;
                });
            },

            controller: [

                '$scope',
                '$timeout',
                'ViewManagerEntity',

                function ($scope, $timeout, ViewManagerEntity) {

                    /*============ PRIVATE VARIABLES =============*/

                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addStaticView = function () {
                        var viewScope = $scope.$new();

                        $scope.viewManager.pushView({
                            id: 'static-view',
                            title: $scope.pageTitle,
                            breadcrumb: $scope.pageTitle,
                            scope: viewScope,
                            template: $('<div></div>').append($scope.transcludeElement).html()
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                    $timeout(addStaticView, 1);

                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
