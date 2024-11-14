@echo off

"C:\\Users\\sojin\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\sojin\\AndroidStudioProjects\\0825\\BeYourEyes_2\\opencv2\\sdk\\libcxx_helper" ^

  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=24" ^
  "-DANDROID_PLATFORM=android-24" ^
  "-DANDROID_ABI=armeabi-v7a" ^
  "-DCMAKE_ANDROID_ARCH_ABI=armeabi-v7a" ^
  "-DANDROID_NDK=C:\\Users\\sojin\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\sojin\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\sojin\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\sojin\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\sojin\\AndroidStudioProjects\\0825\\BeYourEyes_2\\opencv2\\sdk\\build\\intermediates\\cxx\\Debug\\4f6r3j4s\\obj\\armeabi-v7a" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\sojin\\AndroidStudioProjects\\0825\\BeYourEyes_2\\opencv2\\sdk\\build\\intermediates\\cxx\\Debug\\4f6r3j4s\\obj\\armeabi-v7a" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\sojin\\AndroidStudioProjects\\0825\\BeYourEyes_2\\opencv2\\sdk\\.cxx\\Debug\\4f6r3j4s\\armeabi-v7a" ^

  -GNinja ^
  "-DANDROID_STL=c++_shared"
