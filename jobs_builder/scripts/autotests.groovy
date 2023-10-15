timeout(60){
    node("maven"){
        currentBuild.description= "BRANCH=${BRANCH}\nBASE_URL=${BASE_URL}"
        stage("Checkout") {
            checkout scm
        }
        stage('Run tests') {
            def jobs = [:]
            def runnerJobs = "$TEST_TYPE".split(",")

            jobs['ui_autotests'] = {
                node('maven-slave') {
                    stage('Ui tests') {
                        if('ui' in runnerJobs) {
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                build(job: 'ui-tests',
                                        parameters: [
                                                string(name: 'BRANCH', value: BRANCH),
                                                string(name: 'BASE_URL', value: BASE_URL),
                                        ])
                            }
                        } else {
                            echo 'Skipping stage...'
                            Utils.markStageSkippedForConditional('keystone api tests')
                        }
                    }
                }
            }
            jobs['api_autotests'] = {
                node('maven-slave') {
                    stage('api tests') {
                        if('api' in runnerJobs) {
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                build(job: 'ui-tests',
                                        parameters: [
                                                string(name: 'BRANCH', value: BRANCH),
                                                string(name: 'BASE_URI', value: BASE_URL),
                                        ])
                            }
                        } else {
                            echo 'Skipping stage...'
                            Utils.markStageSkippedForConditional('keystone api tests')
                        }
                    }
                }
            }
            jobs['mobile_autotests'] = {
                node('maven-slave') {
                    stage('Mobile tests') {
                        if('mobile' in runnerJobs) {
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                build(job: 'ui-tests',
                                        parameters: [
                                                string(name: 'BRANCH', value: BRANCH),
                                                string(name: 'ADDRESS_SERVER', value: ADDRESS_SERVER),
                                                string(name: 'PLATFORM_NAME', value: PLATFORM_NAME),
                                                string(name: 'PLATFORM_VERSION', value: PLATFORM_VERSION)
                                        ])
                            }
                        } else {
                            echo 'Skipping stage...'
                            Utils.markStageSkippedForConditional('keystone api tests')
                        }
                    }
                }
            }


            parallel jobs
        }
    }
}
