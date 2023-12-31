import org.jenkinsci.plugins.pipeline.modeldefinition.Utils
timeout(60){
    node("maven"){
        currentBuild.description= "BRANCH=${BRANCH}\nBASE_URL=${BASE_URL}"
        stage("Checkout") {
            checkout scm
        }
        stage('Run tests') {
            def jobs = [:]
            def runnerJobs = "$JOB_RUNNER".split(",")
            echo "$JOB_RUNNER"
            jobs['ui_autotests'] = {
                node('maven') {
                    stage('UI tests') {
                        if('ui' in runnerJobs) {
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                build(job: 'ui-autotests',
                                        parameters: [
                                                string(name: 'BRANCH', value: BRANCH),
                                                string(name: 'BASE_URL', value: BASE_URL),
                                        ])
                            }
                        } else {
                            echo 'Skipping stage...'
                            Utils.markStageSkippedForConditional('UI tests')
                        }
                    }
                }
            }
            jobs['api_autotests'] = {
                node('maven') {
                    stage('API tests') {
                        if('api' in runnerJobs) {
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                build(job: 'api-autotests',
                                        parameters: [
                                                string(name: 'BRANCH', value: BRANCH),
                                                string(name: 'BASE_URI', value: BASE_URL),
                                        ])
                            }
                        } else {
                            echo 'Skipping stage...'
                            Utils.markStageSkippedForConditional('API tests')
                        }
                    }
                }
            }
            jobs['mobile_autotests'] = {
                node('maven') {
                    stage('Mobile tests') {
                        if('mobile' in runnerJobs) {
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                build(job: 'mobile-autotests',
                                        parameters: [
                                                string(name: 'BRANCH', value: BRANCH),
                                                string(name: 'ADDRESS_SERVER', value: ADDRESS_SERVER),
                                                string(name: 'PLATFORM_NAME', value: PLATFORM_NAME),
                                                string(name: 'PLATFORM_VERSION', value: PLATFORM_VERSION)
                                        ])
                            }
                        } else {
                            echo 'Skipping stage...'
                            Utils.markStageSkippedForConditional('Mobile tests')
                        }
                    }
                }
            }


            parallel jobs
        }
    }
}
