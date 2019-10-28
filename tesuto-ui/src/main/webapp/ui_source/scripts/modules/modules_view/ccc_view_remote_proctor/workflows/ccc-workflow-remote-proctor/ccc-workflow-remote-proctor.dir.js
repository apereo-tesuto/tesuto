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

    angular.module('CCC.View.RemoteProctor').directive('cccWorkflowRemoteProctor', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                "$location",
                'ViewManagerEntity',

                function ($scope, $location, ViewManagerEntity) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var remoteEventUrl = $location.search();


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addRemoteProctorView = function () {

                        var viewScope = $scope.$new();

                        $scope.viewManager.pushView({
                            id: 'remote-proctor-event',
                            title: 'Remote Proctor Event',
                            breadcrumb: 'Remote Proctor Event',
                            scope: viewScope,
                            template: '<ccc-remote-proctor-event></ccc-remote-proctor-event>'
                        });
                    };

                    var addRemoteProctorAgreement = function () {

                        var viewScope = $scope.$new();

                        $scope.viewManager.pushView({
                            id: 'ccc-remote-proctor-agreement',
                            title: 'Remote Proctor Agreement',
                            breadcrumb: 'Remote Proctor Agreement',
                            scope: viewScope,
                            template: '<ccc-remote-proctor-agreement></ccc-remote-proctor-agreement>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                    if (remoteEventUrl.acknowledge === 'true') {
                        addRemoteProctorView();
                    } else {
                        addRemoteProctorAgreement();
                    }

                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
