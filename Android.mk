LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

#LOCAL_STATIC_JAVA_LIBRARIES := android-common android-support-v13

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := Notebooks
LOCAL_CERTIFICATE := shared

include $(BUILD_PACKAGE)
