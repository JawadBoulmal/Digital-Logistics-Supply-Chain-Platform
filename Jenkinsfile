pipeline {
    agent any // Run on any available agent

    tools {
        // Assumes you have 'Maven 3' configured in
        // Jenkins -> Global Tool Configuration
        maven 'Maven 3'
    }

    stages {
        stage('Clone Repository') {
            steps {
                // Clones the specified repository and branch
                git branch: 'main',
                    url: 'git@github.com:JawadBoulmal/Digital-Logistics-Supply-Chain-Platform.git',
                    credentialsId: '12d9e65e-ac5d-489a-939d-daddb61bd18b'
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    // This 'try' block will catch errors if 'mvn' fails
                    try {
                        sh 'mvn clean package'
                    } catch (e) {
                        // If mvn fails, fail the build immediately
                        error "Maven build failed: ${e.message}"
                    }

                    // --- THIS IS THE FIXED BLOCK ---

                    // 1. Find the list of jar files
                    def jarFiles = findFiles(glob: 'target/*.jar')

                    // 2. SAFETY CHECK: Check if the list is empty
                    if (jarFiles.length > 0) {
                        // 3. If it's NOT empty, get the first file
                        def jarFile = jarFiles[0]
                        env.JAR_PATH = jarFile.path
                        echo "Found JAR: ${env.JAR_PATH}"
                    } else {
                        // 4. If it IS empty, fail the build with a clear error
                        error "Build succeeded, but no .jar file was found in the target/ directory."
                    }
                    // --- END OF FIXED BLOCK ---
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                // Saves the .jar file (called an "artifact") with this
                // build. You can download it from the Jenkins build page.
                archiveArtifacts artifacts: env.JAR_PATH, fingerprint: true
            }
        }
    }

    post {
        // This block runs after all stages are complete
        always {
            echo 'Pipeline finished.'
            // Clean up workspace to save disk space
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded! All tests passed.'
        }
        failure {
            echo 'Pipeline failed. Check the tests.'
        }
    }
}