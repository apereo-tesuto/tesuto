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

    angular.module('CCC.Components').directive('cccModalSessionTimeout', function () {

        return {

            restrict: 'E',

            // you would typically want to pass in the modal instance so you can use it to self close etc..
            scope: {
                modal: "="
            },

            controller: [

                '$scope',

                function ($scope) {


                    /*============ LISTENERS ============*/

                    $scope.$on('IdleEnd', function () {
                        $scope.modal.instance.dismiss();
                    });

                }
            ],

            template: [

                // NOTE: [autofocus] for modals
                '<div autofocus tabindex="-1" id="{{modal.labelId}}">',

                    '<div class="modal-header">',

                        '<h3 class="modal-title text-warning">',
                            '<i class="fa fa-clock-o text-symbol-warning" aria-hidden="true"></i>',
                            ' <span idle-countdown="countdown">Your session will expire in {{countdown}} seconds</span>',
                        '</h3>',

                    '</div>',

                    '<div class="modal-content-wrapper">',
                        // NOTE: Here we add in the contentID
                        '<div class="modal-body" id="{{modal.contentId}}">',
                            '<span>Click or press any key to continue your session.</span>',
                        '</div>',
                    '</div>',

                '</div>'
            ].join('')

        };

    });

})();
