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

    angular.module('CCC.Components').directive('cccModalConfirm', function () {

        return {

            restrict: 'E',

            // you would typically want to pass in the modal instance so you can use it to self close etc..
            scope: {
                modal: '=',
                type: '=',
                headline: '=',
                message: '=',       // can be a single string or an array of message strings
                buttonConfigs: '=',
                onBeforeConfirm: '='
            },

            controller: [

                '$scope',
                '$element',
                '$location',

                function ($scope, $element, $location) {

                    /*============ PRIVATE VARIABLES AND METHODS ===========*/

                    var buttonDefaults = {
                        cancel: {
                            title: 'CCC_COMP.MODALS.BUTTONS.STAY',
                            btnClass: 'btn-primary',
                            btnIcon: false  // you can provide a font-awesome class here... eg fa-star
                        },
                        okay: {
                            title: 'CCC_COMP.MODALS.BUTTONS.LEAVE',
                            btnClass: 'btn-default',
                            btnIcon: false
                        }
                    };


                    /*============ MODEL =============*/

                    $scope.buttons = $.extend(true, {}, buttonDefaults, $scope.buttonConfigs);

                    $scope.iconMap = {
                        info: 'fa-info-circle',
                        warning: 'fa-exclamation-triangle'
                    };

                    $scope.isLoading = false;
                    $scope.isClosing = false;

                    if (!_.isArray($scope.message)) {
                        $scope.message = [$scope.message];
                    }


                    /*============ BEHAVIOR ============*/

                    $scope.cancel = function () {
                        $scope.modal.instance.dismiss();
                    };

                    $scope.continue = function () {

                        $scope.isLoading = true;

                        $scope.onBeforeConfirm(function () {

                            $scope.isLoading = false;
                            $scope.isClosing = true;
                            $scope.modal.instance.close();

                        }, function () {

                            $scope.isLoading = false;
                        });
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('modal.closing', function(event) {

                        if ($scope.isLoading) {
                            event.preventDefault();
                        }
                    });
                }
            ],

            template: [

                // NOTE: [autofocus] for modals
                '<div autofocus tabindex="-1" id="{{modal.labelId}}" class="ccc-modal-alert-{{type}}">',

                    '<div class="modal-header">',

                        '<h3 class="modal-title">',
                            '<i ng-if="type !== \'default\'" class="fa fa {{iconMap[type]}}" aria-hidden="true"></i>',
                            ' <span translate="{{headline}}"></span>',
                        '</h3>',

                    '</div>',

                    '<div class="modal-content-wrapper">',
                        // NOTE: Here we add in the contentID
                        '<div class="modal-body" id="{{modal.contentId}}">',
                            '<p ng-repeat="msg in message" translate="{{msg}}"></p>',
                        '</div>',
                    '</div>',

                '</div>',

                '<div class="modal-footer">',
                    '<button tabindex="0" class="btn {{::buttons.cancel.btnClass}}" ng-click="cancel()" ng-disabled="isLoading || isClosing">',
                        '<i ng-if="buttons.cancel.btnIcon" class="fa {{::buttons.cancel.btnIcon}}" aria-hidden="true"></i>',
                        '<span translate="{{::buttons.cancel.title}}"></span> ',
                    '</button>',
                    '<button tabindex="0" class="btn {{::buttons.okay.btnClass}}" ng-click="continue()" ng-disabled="isLoading || isClosing">',
                        '<i ng-if="buttons.okay.btnIcon && !isLoading" class="noanim fa {{::buttons.okay.btnIcon}}" aria-hidden="true"></i>',
                        '<i ng-if="isLoading" class="noanim fa fa-spin fa-spinner" aria-hidden="true"></i>',
                        '<span translate="{{::buttons.okay.title}}"></span> ',
                    '</button>',
                '</div>'
            ].join('')
        };

    });

})();
