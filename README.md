# OPEN State

OPEN State binds together OPEN API and OPEN Chain and provides extensibility of the whole OPEN Platform.
OPEN State is a hybrid solution that utilizes on-chain and off-chain approaches. It allows solving interoperability issues as well as blockchain scaling.

#### CI/CD process

After new commit to this repository, gitlab start new pipeline based pon .gitlab-ci.yml configuration.

1. As first stage of pipeline, gitlab-ci runs unit-tests with `./gradlew check` command. After after successful, `build/reports` directory will be saved to gitlab artifacts.
2. 2nd stage of pipeline - build .jar file via `./gradlew assemble` command.
3. `package` stage of pipeline will gather docker-image based on docker/Dockerfile.
4. deploy-sprint/deploy-master stages run docker-compose-server.yml based environment on remote server.  
   deploy stages uses SSH connection tunnel to attach local docker/docker-compose commands on to remote docker daemon.  
   Add new environment variable to docker container runtime:  
   a) add new variable to Gitlab CI/CD configuration (settings->CI/CD->Variables)  
   b) add new variable to docker-compose-server.yml file

**Pre-requirements:**

- Remote server docker version: >= 18.09.0
- On GCP VM: disable sshguard, or add all gitlab runners ip's to sshguard whitelist
- Server sshd config (/etc/ssh/sshd_config): MaxSessions 30
- Ssh connection user should be in docker group on remote server
