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

    angular.module('CCC.View.Home').directive('cccUserEditConfirm', function () {

        return {

            restrict: 'E',

            scope: {
                user: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/

                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ==============*/

                    $scope.okay = function () {
                        $scope.$emit('ccc-user-edit-confirm.okay');
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/
                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-xs-12">',
                        '<div id="ccc-user-edit-confirm-success" class="ccc-success-box alert alert-success" role="alert"><h2><span class="fa fa-check-square icon" role="presentation" aria-hidden="true"></span> User Updated</h2></div>',
                    '</div>',
                '</div>',

                '<div class="row">',
                    '<div class="col-md-4 col-sm-6">',
                        '<ccc-system-user class="ccc-user" user="user"></ccc-system-user>',
                    '</div>',
                '</div>',

                '<div class="row row-form-buttons">',
                    '<div class="col-xs-12">',
                        '<button ccc-autofocus class="btn btn-primary btn-min-width" ng-click="okay()" aria-describedby="ccc-user-edit-confirm-success">',
                            '<i class="fa fa-check" aria-hidden="true"></i>',
                            '<span translate="Okay"></span>',
                        '</button>',
                    '</div>',
                '</div>'

            ].join('')
        };

    });

})();
