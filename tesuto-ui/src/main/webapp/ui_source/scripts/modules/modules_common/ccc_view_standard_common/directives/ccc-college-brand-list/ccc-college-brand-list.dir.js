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

    angular.module('CCC.View.Common').directive('cccCollegeBrandList', function () {

        return {

            restrict: 'E',

            scope: {
                colleges: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ============*/

                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.collegeClicked = function (college) {
                        $scope.$emit('ccc-college-brand-list.collegeClicked', college);
                    };


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/
                }
            ],

            template: [

                '<ul class="nav nav-pills nav-stacked">',
                    '<li ng-repeat="college in colleges track by college.cccId" class="ccc-list-item-big">',
                        '<a href="#" class="btn btn-default college text-left" ng-click="collegeClicked(college)" role="button" aria-describedby="{{::describedBy}}">',
                            '<ccc-image image-url="{{college.iconUrl}}" default-url="ui/resources/images/ccc-logomark-small.png" width="32"></ccc-image>',
                            '{{college.name}}',
                        '</a>',
                    '</li>',
                '</ul>'

            ].join('')

        };

    });

})();
