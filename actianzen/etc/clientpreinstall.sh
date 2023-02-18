#!/bin/sh

echo "+++++ Checking current user rights..."

if [ "$(id -u root)" = "$(id -u)" ] ; then
    echo "Passed..."
else
    echo "ERROR: The current user id is not root (`id|awk '{print $1}'`)."
    exit 1
fi
echo


if [ "X$ACTIANZEN_ROOT" = "X" ] ; then
    ACTIANZEN_ROOT=/usr/local/actianzen
fi
PATH=$ACTIANZEN_ROOT/bin:$PATH
if [ $(uname -s) = "Darwin" ]
then
    DYLD_LIBRARY_PATH=$ACTIANZEN_ROOT/bin:$ACTIANZEN_ROOT/lib:$ACTIANZEN_ROOT/lib64:$DYLD_LIBRARY_PATH
    export DYLD_LIBRARY_PATH
    SO_EXTENSION="dylib"
else
    LD_LIBRARY_PATH=$ACTIANZEN_ROOT/bin:$ACTIANZEN_ROOT/lib64:$LD_LIBRARY_PATH
    SO_EXTENSION="so"
fi


echo "+++++ Checking for previous installs..."

# PSQL-6812 - v14 incompatible with v13 and earlier.
if [ -f /usr/local/psql/lib64/libpsqlmif.${SO_EXTENSION} ] ; then
    echo "ERROR: An incompatible version of PSQL is currently installed.  Please uninstall the incompatible version before installing Zen."
    exit 1
fi
if [ -f $ACTIANZEN_ROOT/bin/mkded ] ; then
    echo "ERROR: 64-bit Server already installed.  Uninstall Zen Server to proceed with client install."
    exit 1
fi
echo "Passed..."
echo

# PSQL-6981 - Cannot install v14 if legacy username is running any process.
echo "+++++ Checking for running processes owned by user 'psql'..."
PROC_LIST_HEADERS=`ps -ef | head -n1`
LEGACY_OWNER_PROCS=`ps -fu psql 2>/dev/null | grep psql | grep -v grep`
if [ "X$LEGACY_OWNER_PROCS" != "X" ] ; then
    echo "ERROR: One or more processes owned by user 'psql' is currently running. Please stop all processes owned by user 'psql' prior to installing Zen."
    echo "Running processes owned by user 'psql':"
    echo "$PROC_LIST_HEADERS"
    echo "$LEGACY_OWNER_PROCS"
    echo
    exit 1
fi
echo "Passed..."
echo

echo "+++++ Checking for system dependencies..."

# Default values assume 64-bit is x86_64 and 32-bit is x86.
ELF64_TEXT="ELF 64-bit LSB"
ELF64_LIB="/lib64/ld-linux-x86-64.${SO_EXTENSION}.2"
ELF32_TEXT="ELF 32-bit LSB"
ELF32_LIB="/lib/ld-linux.${SO_EXTENSION}.2"

UNAME_PROC=`uname -mp`
echo $UNAME_PROC | grep "aarch64" >/dev/null
if [ $? -eq 0 ] ; then
    # 64-bit ARM
    ELF64_LIB="/lib64/ld-linux-aarch64.${SO_EXTENSION}.1 /lib/ld-linux-aarch64.${SO_EXTENSION}.1"
fi
echo $UNAME_PROC | grep "armv" >/dev/null
if [ $? -eq 0 ] ; then
    # 32-bit ARM
    ELF32_LIB="/lib/ld-linux-armhf.${SO_EXTENSION}.3"
fi

if [ $(uname -s) != "Darwin" ]
then
    if [ -f $ACTIANZEN_ROOT/lib64/libpsqlmif.${SO_EXTENSION}.*.*.*.* ] ; then
        file -L $ELF32_LIB | grep "$ELF32_TEXT" >/dev/null 2>&1
        if [ $? -ne 0 ] ; then
            echo "ERROR: $ELF32_LIB not found. This package requires 32-bit runtime library support."
            exit 1
        fi
    fi
    if [ -f $ACTIANZEN_ROOT/lib64/libpsqlmif.${SO_EXTENSION}.* ] ; then
        file -L $ELF64_LIB | grep "$ELF64_TEXT" >/dev/null 2>&1
        if [ $? -ne 0 ] ; then
            echo "ERROR: $ELF64_LIB not found. This package requires 64-bit runtime library support."
            exit 1
        fi
    fi

fi

echo "Passed..."
echo
