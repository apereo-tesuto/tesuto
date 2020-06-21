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

    angular.module('CCC.API.SubjectAreas').factory('SubjectAreaCourseClass', [

        '$q',
        'Cloneable',
        'CCCUtils',
        'SubjectAreasAPIService',
        'CoursesAPIService',
        'CompetencyGroupClass',

        function ($q, Cloneable, CCCUtils, SubjectAreasAPIService, CoursesAPIService, CompetencyGroupClass) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            /*============ CLASS DECLARATION ============*/

            var SubjectAreaCourseClass = function (configs_in) {

                var defaults = {

                    courseId: null, // the system uuid
                    disciplineId: null,
                    cb21Code: null, // transfer level
                    courseGroup: null,

                    subject: '',
                    number: '',
                    name: '',
                    cid: '',
                    auditId: 0,
                    description: '',
                    competencyGroupsIds: [],
                    sisTestCode: '',

                    mmapEquivalentCode: null,

                    competencyGroups: null, // MUST BE NULL BE DEFAULT logic in fetchCompetencyGroups will check this
                    competencyGroupLogic: '',
                    deleting: false
                };

                // merge in the defaults onto the instance
                var that = this;
                // extend from the Cloneable class
                Cloneable.call(that);
                $.extend(true, that, defaults, configs_in || {});


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                // generate useful meta data from initial config data (from server etc...)
                var initialize = function () {};


                /*=============== PUBLIC PROPERTIES =============*/


                /*=============== PUBLIC METHODS =============*/

                that.getCompetencyIds = function () {
                    return _.map(that.competencyGroups, function (competencyGroup) {
                        return competencyGroup.competencyGroupId;
                    });
                };

                // method used to send data for creation and update
                that.serialize = function () {

                    // this is the format the backend needs for creation and updating
                    var serializeModel = {
                        courseId: that.courseId,
                        subject: that.subject,
                        number: that.number,
                        name: that.name,
                        cid: that.cid,
                        description: that.description,
                        cb21Code: that.cb21Code,
                        courseGroup: that.courseGroup,
                        competencyGroups: that.competencyGroups,
                        mmapEquivalentCode: that.mmapEquivalentCode,
                        sisTestCode: that.sisTestCode,
                        auditId: that.auditId,
                        competencyGroupLogic: that.competencyGroupLogic
                    };

                    return serializeModel;
                };

                that.addCompetencyGroup = function (competencyGroup) {
                    that.competencyGroups.push(competencyGroup);
                };

                that.deleteCompetencyGroup = function (competencyGroup) {

                    var foundIndex = -1;

                    _.find(that.competencyGroups, function (competencyGroupItem, index) {

                        var isFound = competencyGroupItem.competencyGroupId === competencyGroup.competencyGroupId;
                        if (isFound) {
                            foundIndex = index;
                        }
                        return isFound;
                    });

                    if (foundIndex !== -1) {
                        that.competencyGroups.splice(foundIndex, 1);
                    }
                };

                that.fetchCompetencyGroups = function (forceRefresh) {

                    if (!forceRefresh && that.competencyGroups !== null) {

                        // this could have been initialized with json for competency groups... we need to turn them into class instances
                        _.each(that.competencyGroups, function (competencyGroup, index) {
                            if (!(competencyGroup instanceof CompetencyGroupClass)) {
                                that.competencyGroups[index] = CCCUtils.coerce('CompetencyGroupClass', competencyGroup);
                            }
                        });

                        return $q.when(that.competencyGroups);

                    } else {

                        return CoursesAPIService.getCompetencyGroups(that.courseId).then(function (competencyGroups) {
                            that.competencyGroups = CCCUtils.coerce('CompetencyGroupClass', competencyGroups);
                        });
                    }
                };

                that.create = function () {

                    return SubjectAreasAPIService.subjectAreaCourses.create(that.disciplineId, that.serialize()).then(function (courseId) {

                        // once created merge on properties created server side during the create process
                        that.courseId = courseId;
                    });
                };

                that.update = function () {
                    return SubjectAreasAPIService.subjectAreaCourses.update(that.disciplineId, that.courseId, that.serialize());
                };

                that.save = function () {

                    if (that.courseId !== null) {
                        return that.update();
                    } else {
                        return that.create();
                    }
                };

                that.delete = function () {
                    that.deleting = true;

                    // if there is a courseId, great, let's persist the delete on the server
                    if (that.courseId) {

                        return SubjectAreasAPIService.subjectAreaCourses.delete(that.disciplineId, that.cb21Code, that.courseId).finally(function () {
                            that.deleting = false;
                        });

                    // otherwise we just immediatly say yes delete is done because this does not have a persisted courseId on the server
                    } else {
                        that.deleting = false;
                        return $q.when(true);
                    }
                };


                /*=============== INITIALIZTION =============*/

                initialize();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return SubjectAreaCourseClass;
        }
    ]);

})();
