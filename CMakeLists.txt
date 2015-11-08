cmake_minimum_required(VERSION 2.8.3)

project(common_driver)
set(PACKAGE_PATH "org/rosbuilding")

find_package(catkin REQUIRED
  rosjava_build_tools

  rosjava_dynamic_reconfigure
  rosbuilding_msgs
  media_msgs
  heater_msgs
  network_zeroconf
)

catkin_rosjava_setup(publishMavenJavaPublicationToMavenRepository)

catkin_package()

install(DIRECTORY ${CATKIN_DEVEL_PREFIX}/${CATKIN_GLOBAL_MAVEN_DESTINATION}/${PACKAGE_PATH}/${PROJECT_NAME}/
    DESTINATION ${CATKIN_GLOBAL_MAVEN_DESTINATION}/${PACKAGE_PATH}/${PROJECT_NAME})
