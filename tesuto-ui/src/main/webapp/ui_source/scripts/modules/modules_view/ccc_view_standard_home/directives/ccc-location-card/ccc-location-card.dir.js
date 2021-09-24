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

    angular.module('CCC.View.Home').directive('cccLocationCard', function () {

        return {

            restrict: 'E',

            scope: {
                location: '='
            },

            template: [

                '<div class="ccc-location-card-location">',
                    '<h3 class="ccc-location-card-name">{{::location.name}}</h3>',
                    '<div class="ccc-location-card-today">',
                        '<h4>Today</h4>',
                        '<div class="row">',
                            '<div class="col-xs-3 text-center">',
                                '<div class="ccc-location-card-metric activations">',
                                    '<span class="ccc-location-card-number">{{::location.total}}</span>',
                                    '<span class="ccc-location-card-text">Activations</span>',
                                '</div>',
                            '</div>',
                            '<div class="col-xs-3 text-center">',
                                '<div class="ccc-location-card-metric ready">',
                                    '<span class="ccc-location-card-number">{{::location.READY}}</span>',
                                    '<span class="ccc-location-card-text">Ready</span>',
                                '</div>',
                            '</div>',
                            '<div class="col-xs-3 text-center">',
                                '<div class="ccc-location-card-metric in-progress">',
                                    '<span class="ccc-location-card-number">{{::location.IN_PROGRESS}}</span>',
                                    '<span class="ccc-location-card-text">In Progress</span>',
                                '</div>',
                            '</div>',
                            '<div class="col-xs-3 text-center">',
                                '<div class="ccc-location-card-metric completed">',
                                    '<span class="ccc-location-card-number">{{::location.COMPLETE}}</span>',
                                    '<span class="ccc-location-card-text">Completed</span>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')
        };
    });

})();
