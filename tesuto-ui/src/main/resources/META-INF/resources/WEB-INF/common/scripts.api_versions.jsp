<script>

    // NOTE: Null value in the map below means that endpoint will use DEFAULT_VERSION
    // NOTE: If it is not in the map below that endpoint will also use DEFAULT_VERSION
    // NOTE: the key should be the path set when a "VersionableAPIClass" is instantiated

    var DEFAULT_VERSION = 'WARNING_VERSION_NOT_SET_FOR_API'; // let's not set the default api version to make mis-configured api errors more obvious

    var API_VERSION_MAP = {
        'activations': {
            baseUrl: '${activationServiceBaseUrl}',
            version: 'v1'
        },
        'activationSearch': {
            baseUrl: '${activationServiceBaseUrl}',
            version: 'v1'
        },
        'assessments': {
            baseUrl: '${contentServiceBaseUrl}',
            version: 'v1'
        },
        'accommodations': {
            baseUrl: '${adminServiceBaseUrl}',
            version: 'v1'
        },
        'students': {
            baseUrl: '${adminServiceBaseUrl}',
            version: 'v2'
        },
        'user': {
            baseUrl: '${adminServiceBaseUrl}',
            version: 'v1'
        },
        'assessmentSessions': {
            baseUrl: '${deliveryServiceBaseUrl}',
            version: 'v1'
        },//previewServiceBaseUrl
        'previewAssessmentSessions': {
            baseUrl: '${deliveryServiceBaseUrl}',
            version: 'v1'
        },
        'passcodes': {
            baseUrl: '${activationServiceBaseUrl}',
            version: 'v1'
        },
        'assessmentMetadata': {
            baseUrl: '${contentServiceBaseUrl}',
            version: 'v1'
        },
        'activityLog': {
            baseUrl: '${deliveryServiceBaseUrl}',
            version: 'v1'
        },
        'user': {
            baseUrl: '${adminServiceBaseUrl}',
            version: 'v1'
        },
        'roles': {
            baseUrl: '${adminServiceBaseUrl}',
            version: 'v1'
        },
        'activationBatch': {
            baseUrl: '${activationServiceBaseUrl}',
            version: 'v1'
        },
        'courses': {
            baseUrl: '${placementServiceBaseUrl}',
            version: 'v1'
        },
        'districts': {
            baseUrl: '${adminServiceBaseUrl}',
            version: 'v1'
        },
        'colleges': {
            baseUrl: '${adminServiceBaseUrl}',
            version: 'v1'
        },
        'subject-areas': {
            baseUrl: '${placementServiceBaseUrl}',
            version: 'v1'
        },
        'competencyMapDiscipline': {
            baseUrl: '${contentServiceBaseUrl}',
            version: 'v1'
        },
        'competencyMaps': {
            baseUrl: '${contentServiceBaseUrl}',
            version: 'v1'
        },
        'competencyGroups': {
            baseUrl: '${placementServiceBaseUrl}',
            version: 'v1'
        },
        'collegeAttributes': {
            baseUrl: '${adminServiceBaseUrl}',
            version: 'v1'
        },
        'testLocations': {
            baseUrl: '${adminServiceBaseUrl}',
            version: 'v1'
        },
        'placement': {
            baseUrl: '${placementServiceBaseUrl}',
            version: 'v1'
        },
        'placementColleges': {
            baseUrl: '${placementServiceBaseUrl}',
            version: 'v1'
        },
        'testEvent': {
            baseUrl: '${activationServiceBaseUrl}',
            version: 'v1'
        },
        'remoteProctor': {
            baseUrl: '${adminServiceBaseUrl}',
            version: 'v1'
        },
        'competencyDisciplines': {
            baseUrl: '${placementServiceBaseUrl}',
            version: 'v1'
        },
        'rulesColleges': {
            baseUrl: '${rulesServiceBaseUrl}',
            version: 'v1'
        },
        'classReport': {
            baseUrl: '${deliveryServiceBaseUrl}',
            version: 'v1'
        },
        'placementRequest': {
            baseUrl: '${placementServiceBaseUrl}',
            version: 'v1'
        },
        'ui': {
            baseUrl: '${uiServiceBaseUrl}',
            version: 'v1'
        },
        'dashboard': {
            baseUrl: '${dashboardServiceBaseUrl}',
            version: 'v1'
        }

    };

</script>
