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

    // set the current user from the global user object in the jsp
    angular.module('CCC.View.Student').config([

        'CurrentStudentServiceProvider',
        'SESSION_CONFIGS',

        function (CurrentStudentServiceProvider, SESSION_CONFIGS) {

            // It's tricky to attach colleges to the student object server side so before we load the student
            // object into the CurrentStudentServiceProvider, we pull colleges from a sibling attribute on session configs
            SESSION_CONFIGS.student.colleges = SESSION_CONFIGS.studentCollegesInfo;

            CurrentStudentServiceProvider.setStudent(SESSION_CONFIGS.student);
        }
    ]);

})();
