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

    angular.module('CCC.View.Common').directive('cccModalRemoteProctorPasscode', function () {

        return {

            restrict: 'E',

            // you would typically want to pass in the modal instance so you can use it to self close etc..
            scope: {
                modal: "=",
                passcode: "="
            },

            controller: [

                '$scope',
                '$timeout',

                function ($scope, $timeout) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ===========*/

                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.done = function () {
                        $scope.modal.instance.dismiss();
                    };


                    /*============ LISTENERS ===========*/

                    /*============ INITIALIZATION ===========*/

                    // automatically close modal after 2 minutes
                    $timeout(function () {
                        $scope.modal.instance.dismiss();
                    }, 120000);

                }
            ],

            template: [

                '<p class="text-warning"><span class="fa fa-clock-o icon" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_STANDARD_COMMON.CCC-MODAL-REMOTE-PROCTOR-PASSCODE.WARNING"></span></p>',

                '<div class="well">',
                    '<h5><strong class="text-success">{{passcode}}</strong></h5>',
                '</div>',

                '<div class="modal-footer">',
                    '<button autofocus ng-click="done()" class="btn btn-primary"><span class="fa fa-check-circle" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_STANDARD_COMMON.CCC-MODAL-REMOTE-PROCTOR-PASSCODE.DONE"></span></button>',
                '</div>'

            ].join('')

        };

    });

})();
