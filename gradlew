#!/bin/sh

#
# Copyright © 2015-2021 the original authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
#
#  Gradle start up script for UN*X
#
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ]; do
    ls="$(ls -ld "$PRG")"
    link="$(expr "$ls" : '.*-> \(.*\)$')"
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG="$(dirname "$PRG")/$link"
    fi
done
SAVED="$PWD"
cd "$(dirname "$PRG")" >/dev/null
APP_HOME="$PWD"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME="$(basename "$0")"

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn() {
    echo "$*"
}

die() {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
darwin=false
ios=false
mingw=false
nonstop=false
case "$(uname)" in
  CYGWIN*) cygwin=true ;;    
  Darwin*) darwin=true
           case "$(uname -m)" in
             arm64) ios=true ;;    
           esac ;;            
  MINGW*) mingw=true ;;       
  NONSTOP*) nonstop=true ;;   
esac

# For Cygwin, ensure paths are in UNIX format before anything is touched.
if $cygwin; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
fi

# Attempt to find Java
if [ -z "$JAVA_HOME" ]; then
    if $darwin && [ -z "$JAVA_HOME" ] && [ -x "/usr/libexec/java_home" ]; then
        # Mac OS X specific java_home script exists
        JAVA_HOME="$(/usr/libexec/java_home)"
    fi
fi

# Split JAVA_OPTS and GRADLE_OPTS into an array, following the shell quoting and substitution rules
IFS=
set -f
_GRADLE_OPTS=($GRADLE_OPTS)
_JAVA_OPTS=($JAVA_OPTS)
set +f
IFS=" "

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ]; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME\n\nPlease set the JAVA_HOME variable in your environment to match the\nlocation of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.\n\nPlease set the JAVA_HOME variable in your environment to match the\nlocation of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ]; then
    # Get kernel limit
    if $darwin || $nonstop; then
        MAX_FD="$(ulimit -n)"
    elif $cygwin || $mingw; then
        # Windows/Cygwin environments automatically set the file descriptor limit.
        MAX_FD="-1"
    else
        MAX_FD="$(ulimit -Hn)"
    fi
fi
if [ "$MAX_FD" != "-1" ]; then
    ulimit -n $MAX_FD || warn "Could not set maximum file descriptor limit: $MAX_FD"
fi

# For Darwin, add options to specify how the application appears in the dock
if $darwin; then
    GRADLE_OPTS="${_GRADLE_OPTS[@]} -Xdock:name=$APP_NAME -Xdock:icon=""$APP_HOME/media/gradle.icns"""
fi

# For Cygwin or MSYS, switch paths to Windows format before running java
if $cygwin || $mingw; then
    APP_HOME="$(cygpath --path --mixed "$APP_HOME")"
    JAVA_HOME="$(cygpath --path --mixed "$JAVA_HOME")"
    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=""
    for i in $(echo -n "${PATH}" | xargs -n 1 dirname); do
        if [ -d "$i" ]; then
            ROOTDIRSRAW="$ROOTDIRSRAW $i"
        fi
    done
    ROOTDIRS="$(echo -n "$ROOTDIRSRAW" | xargs -n 1 dirname | sort | uniq | tr "\n" ";")"
    if [ -z "$ROOTDIRS" ]; then
        ROOTDIRS="$(cygpath --path --mixed "$SYSTEMROOT\System32")"  
    fi
    # Now convert the arguments - kludge to limit ourselves to /bin/sh
    for i in "${_JAVA_OPTS[@]}" "${_GRADLE_OPTS[@]}" "$@"; do
        CHECK="$(echo "$i" | grep -E "^(/|[a-zA-Z]:|\\\\|//)")"
        if [ -n "$CHECK" ]; then
            if [ -d "$i" ]; then
                i="$(cygpath --path --mixed "$i")"
            elif [ -f "$i" ]; then
                i="$(cygpath --path --mixed "$i")"
            fi
        fi
        GRADLE_OPTS="$GRADLE_OPTS $i"
    done
else
    # Add default JVM options.
    GRADLE_OPTS="${_JAVA_OPTS[@]} ${_GRADLE_OPTS[@]} $GRADLE_OPTS"
fi

# Collect all arguments for the java command, following the shell quoting and substitution rules
eval set -- "$@"

# by default we should be in the correct project dir, but when run from Finder on Mac, the cwd is wrong
if [ "$(uname)" = "Darwin" ] && [ "$HOME" = "$PWD" ]; then
    cd "$(dirname "$0")"
fi

# Execute Gradle
"$JAVACMD" "$DEFAULT_JVM_OPTS" "$JAVA_OPTS" "$GRADLE_OPTS" "-Dorg.gradle.appname=$APP_BASE_NAME" -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"