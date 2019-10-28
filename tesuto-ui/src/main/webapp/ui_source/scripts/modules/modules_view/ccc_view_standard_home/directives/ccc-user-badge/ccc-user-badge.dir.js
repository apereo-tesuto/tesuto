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

    angular.module('CCC.View.Home').directive('cccUserBadge', function () {

        return {

            restrict: 'E',

            scope: {
                user: '='
            },

            template: [
                '<div class="well">',
                    '<div class="identity">',
                        '<div class="avatar">',
                            '<img src="ui/resources/images/demo/avatar-admin.jpg" alt="Your avatar">',
                        '</div>',
                        '<div class="user-name">',
                            '<span class="first-name"> {{user.displayName}} </span> ',
                            '<span ng-if="securityGroups">(<span class="ccc-secondary-info ccc-lowercase">{{user.securityGroups.join(\', \')}}</span>)</span>',
                        '</div>',
                        '<ul class="list list-unstyled colleges">',
                            '<li class="college">',
                                ' <img class="logo" src="ui/resources/images/demo/santa-rosa.png" alt="Santa Rosa Junior College logo">',
                                ' <span class="name">Santa Rosa Junior College</span>',
                            '</li>',
                        '</ul>',
                    '</div>',
                '</div>'
            ].join('')

        };

    });

})();





