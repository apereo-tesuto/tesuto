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

    angular.module('CCC.View.Home').directive('cccSubjectAreaCourseCardLight', function () {

        return {

            restrict: 'E',

            scope: {
                course: '='
            },

            template: [

                '<div class="course-card">',
                    '<h4 class="title name">',
                        '<span class="data">{{course.name}}</span>',
                    '</h4>',
                    '<h5 class="subtitle">',
                        '<span class="subject"><span class="data">{{::course.subject}}</span></span>',
                        '<span class="number"><span class="data">{{::course.number}}</span></span>',
                    '</h5>',
                    '<p class="description"><span class="data">{{::course.description}}</span></p>',
                    '<div class="cid"><strong>C-ID:</strong> <span class="data">{{::course.cid}}</span></div>',
                    '<div class="transfer-level"><strong>Transfer Level:</strong> <span class="data">{{::course.cb21Code}}</span></div>',
                '</div>'

            ].join('')

        };

    });

})();
