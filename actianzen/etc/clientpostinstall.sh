#!/bin/sh

echo "+++++ Checking current user rights..."

if [ "$(id -u root)" = "$(id -u)" ] ; then
    :
else
    echo "ERROR: The current user id is not root (`id|awk '{print $1}'`)."
    exit 1
fi
echo


if [ "X$ACTIANZEN_ROOT" = "X" ] ; then
    ACTIANZEN_ROOT=/usr/local/actianzen
fi
# PSQL-6827: For restoring legacy v13 or earlier dbnames.cfg.
if [ "X$PVSW_ROOT" = "X" ] ; then
    PVSW_ROOT=/usr/local/psql
fi

# Determine if system libstdc meets Zen version requirements and remove
# bundled version if it does.
if [ $(uname -s) != "Darwin" ]; then
    echo "+++++ Checking system libstdc++ version support..."
    # Verify readelf is available.
    READELF=`which readelf`
    if [ ! -f "$READELF" ]; then
        echo "WARNING: Unable to determine system libstdc++ version support (missing readelf command)."
        echo "The libstdc++ libraries bundled with the Zen install will be used, which may prevent the"
        echo "opening of documentation from within ZenCC and possibly other issues running Zen utilities."
    else
        ZEN_REQUIRED_LIBSTDC_VER=3.4.21
        ZEN_FILE32_TO_CHECK=$ACTIANZEN_ROOT/bin/psregsvr
        ZEN_FILE64_TO_CHECK=$ACTIANZEN_ROOT/bin/psregsvr64

        # First rename the bundled libstdc++ libs in case $ACTIANZEN_ROOT is
        # already in the path so the ldd check below will locate the system
        # library instead of the Zen-provided library (or not find it at all).
        if [ -f $ACTIANZEN_ROOT/lib/libstdc++.so.6 ]; then
            mv $ACTIANZEN_ROOT/lib/libstdc++.so.6 $ACTIANZEN_ROOT/lib/libstdc++.so.6.zen
        fi
        if [ -f $ACTIANZEN_ROOT/lib64/libstdc++.so.6 ]; then
            mv $ACTIANZEN_ROOT/lib64/libstdc++.so.6 $ACTIANZEN_ROOT/lib64/libstdc++.so.6.zen
        fi

        # Now locate 32-bit system library path from file dependencies for $ZEN_FILE32_TO_CHECK.
        if [ -f $ZEN_FILE32_TO_CHECK ]; then
            echo "Obtaining libstdc++ dependencies from 32-bit $ZEN_FILE32_TO_CHECK"
            SYS_INSTALLED_LIBSTDC32_PATH=`ldd $ZEN_FILE32_TO_CHECK | grep libstdc++.so.6 | cut -d " " -f 3`
            if [ -f "$SYS_INSTALLED_LIBSTDC32_PATH" ]; then
                echo "Checking for required version info in $SYS_INSTALLED_LIBSTDC32_PATH"
                FOUND_ZEN_LIBSTDC32_DEP=`readelf -V $SYS_INSTALLED_LIBSTDC32_PATH | grep GLIBCXX_$ZEN_REQUIRED_LIBSTDC_VER`
                if [ "X$FOUND_ZEN_LIBSTDC32_DEP" = "X" ]; then
                    echo "Required 32-bit version support not found, Zen will use 32-bit libstdc++.so.6 from install."
                    # Revert previous rename of libraries bundled with Zen install.
                    if [ -f $ACTIANZEN_ROOT/lib/libstdc++.so.6.zen ]; then
                        # Revert previous rename of libraries bundled with Zen install.
                        mv $ACTIANZEN_ROOT/lib/libstdc++.so.6.zen $ACTIANZEN_ROOT/lib/libstdc++.so.6
                    fi
                else
                    echo "Required 32-bit version support found, Zen will use $SYS_INSTALLED_LIBSTDC32_PATH"
                    # Must also remove previously renamed file or it will get a simlink created
                    # for it later in this script.
                    if [ -f $ACTIANZEN_ROOT/lib/libstdc++.so.6.zen ]; then
                        rm -f $ACTIANZEN_ROOT/lib/libstdc++.so.6.zen
                    fi
                fi
            else
                echo "Required 32-bit version support not found, Zen will use 32-bit libstdc++.so.6 from install."
                # Revert previous rename of libraries bundled with Zen install.
                if [ -f $ACTIANZEN_ROOT/lib/libstdc++.so.6.zen ]; then
                    # Revert previous rename of libraries bundled with Zen install.
                    mv $ACTIANZEN_ROOT/lib/libstdc++.so.6.zen $ACTIANZEN_ROOT/lib/libstdc++.so.6
                fi
            fi
        else
            echo "The 32-bit $ZEN_FILE32_TO_CHECK is not present in the Zen install package, skipping 32-bit libstdc++ dependency check."
        fi

        # Now locate 64-bit system library path from file dependencies for $ZEN_FILE64_TO_CHECK.
        if [ -f $ZEN_FILE64_TO_CHECK ]; then
            echo "Obtaining libstdc++ dependencies from 64-bit $ZEN_FILE64_TO_CHECK"
            SYS_INSTALLED_LIBSTDC64_PATH=`ldd $ZEN_FILE64_TO_CHECK | grep libstdc++.so.6 | cut -d " " -f 3`
            if [ -f "$SYS_INSTALLED_LIBSTDC64_PATH" ]; then
                echo "Checking for required version info in $SYS_INSTALLED_LIBSTDC64_PATH"
                FOUND_ZEN_LIBSTDC64_DEP=`readelf -V $SYS_INSTALLED_LIBSTDC64_PATH | grep GLIBCXX_$ZEN_REQUIRED_LIBSTDC_VER`
                if [ "X$FOUND_ZEN_LIBSTDC64_DEP" = "X" ]; then
                    echo "Required 64-bit version support not found, Zen will use 64-bit libstdc++.so.6 from install."
                    # Revert previous rename of libraries bundled with Zen install.
                    if [ -f $ACTIANZEN_ROOT/lib64/libstdc++.so.6.zen ]; then
                        # Revert previous rename of libraries bundled with Zen install.
                        mv $ACTIANZEN_ROOT/lib64/libstdc++.so.6.zen $ACTIANZEN_ROOT/lib64/libstdc++.so.6
                    fi
                else
                    echo "Required 64-bit version support found, Zen will use $SYS_INSTALLED_LIBSTDC64_PATH"
                    # Must also remove previously renamed file or it will get a simlink created
                    # for it later in this script.
                    if [ -f $ACTIANZEN_ROOT/lib64/libstdc++.so.6.zen ]; then
                        rm -f $ACTIANZEN_ROOT/lib64/libstdc++.so.6.zen
                    fi
                fi
            else
                echo "Required 64-bit version support not found, Zen will use 64-bit libstdc++.so.6 from install."
                # Revert previous rename of libraries bundled with Zen install.
                if [ -f $ACTIANZEN_ROOT/lib64/libstdc++.so.6.zen ]; then
                    # Revert previous rename of libraries bundled with Zen install.
                    mv $ACTIANZEN_ROOT/lib64/libstdc++.so.6.zen $ACTIANZEN_ROOT/lib64/libstdc++.so.6
                fi
            fi
        else
            echo "The 64-bit $ZEN_FILE64_TO_CHECK is not present in the Zen install, skipping 64-bit dependency check."
        fi
    fi
fi
echo

PATH=$ACTIANZEN_ROOT/bin:$PATH

if [ $(uname -s) = "Darwin" ]
then
    DYLD_LIBRARY_PATH=$ACTIANZEN_ROOT/bin:$ACTIANZEN_ROOT/lib64:$DYLD_LIBRARY_PATH
    export DYLD_LIBRARY_PATH
    SO_EXTENSION="dylib"
    SU_ARGS="-l"
else
    LD_LIBRARY_PATH=$ACTIANZEN_ROOT/bin:$ACTIANZEN_ROOT/lib:$ACTIANZEN_ROOT/lib64:$LD_LIBRARY_PATH
    export LD_LIBRARY_PATH
    SO_EXTENSION="so"
    SU_ARGS=
fi

export ACTIANZEN_ROOT
if [ -f $ACTIANZEN_ROOT/etc/postinstall.cfg ] ; then
    . $ACTIANZEN_ROOT/etc/postinstall.cfg
fi
SU="su -"
BASH_ENV=
USERNAME=zen-svc
GROUPNAME=zen-data
ADMGROUPNAME=zen-adm
# PSQL-6827: If legacy v13 or earlier previously installed and then uninstalled,
#            the legacy groups still exists with the same IDs.
LEGACY_USERNAME=psql
LEGACY_GROUPNAME=pvsw
LEGACY_ADMGROUPNAME=pvsw-adm
LEGACY_HOMEDIR=`grep "^$LEGACY_USERNAME:" /etc/passwd | cut -d: -f6`
PS=/bin/ps
PSPARAMS=ax
SEM=sem
MEM=shm
ECHO=/bin/echo
GRPFLAG="-g 5000"
ADMGRPFLAG="-g 5001"
USERSHL=/bin/bash
USERSHLPROFILE1=.bash_profile
USERSHLPROFILE2=.bashrc
SECDIR=/lib/security
if [ ! -d "$SECDIR" ] ; then
    if [ $(uname -s) = "Darwin" ]
    then
        SECDIR=/usr/lib/pam
    else
        SECDIR=/usr/lib/security
    fi
fi
if [ ! -d "$SECDIR" ] ; then
    SECDIR=
fi
SORT=/bin/sort
if [ ! -x $SORT ] ; then
    SORT=/usr/bin/sort
fi
LDCONFIG=/sbin/ldconfig
if [ ! -x $LDCONFIG ] ; then
    LDCONFIG=/usr/sbin/ldconfig
fi
PWDBLIB=pam_pwdb.${SO_EXTENSION}
PWDBLIBPARAM="shadow nullok"
USERSHLPROFILE3=.cshrc
SELINUX_ENABLED=/usr/sbin/selinuxenabled


echo "+++++ Checking user and group configuration..."
# PSQL-6827/PSQL-8447: Move up checks for legacy and current user/groups. The Legacy user
# and groups will only be migrated to current user and groups if both don't already exist.
if [ $(uname -s) = "Darwin" ]
then
    LEGACY_PUSER_FOUND=`dscl . -list /Users UniqueID | awk '($1 == "'"$LEGACY_USERNAME"'")'`
    LEGACY_PVSW_GRP_FOUND=`dscl . -list /Groups PrimaryGroupID | awk '($1 == "'"$LEGACY_GROUPNAME"'")'`
    LEGACY_ADM_GRP_FOUND=`dscl . -list /Groups PrimaryGroupID | awk '($1 == "'"$LEGACY_ADMGROUPNAME"'")'`
    UPVSWEXISTS=`dscl . -list /Users UniqueID | awk '($1 == "'"$USERNAME"'")'`
    GPVSWEXISTS=`dscl . -list /Groups PrimaryGroupID | awk '($1 == "'"$GROUPNAME"'")'`
    GPVSWADMEXISTS=`dscl . -list /Groups PrimaryGroupID | awk '($1 == "'"$ADMGROUPNAME"'")'`
else
    LEGACY_PUSER_FOUND=`grep "^$LEGACY_USERNAME:" /etc/passwd`
    LEGACY_PVSW_GRP_FOUND=`grep "^$LEGACY_GROUPNAME:" /etc/group`
    LEGACY_ADM_GRP_FOUND=`grep "^$LEGACY_ADMGROUPNAME:" /etc/group`
    UPVSWEXISTS=`grep "^$USERNAME:" /etc/passwd`
    GPVSWEXISTS=`grep "^$GROUPNAME:" /etc/group`
    GPVSWADMEXISTS=`grep "^$ADMGROUPNAME:" /etc/group`
fi
# PSQL-8447: Databases don't get created during upgrade from legacy version if
# both users exist. Also check groups.
# -- Both users exist.
if [ "X$UPVSWEXISTS" != "X" ] && [ "X$LEGACY_PUSER_FOUND" != "X" ] ; then
    echo "ERROR: The legacy user $LEGACY_USERNAME was found but cannot be"
    echo "converted to new user $USERNAME because $USERNAME already exists."
    echo "Please delete one of the users and then restart the installation."
    exit 1 ;
fi
# -- Both groups exist.
if [ "X$GPVSWEXISTS" != "X" ] && [ "X$LEGACY_PVSW_GRP_FOUND" != "X" ] ; then
    echo "ERROR: The legacy group $LEGACY_GROUPNAME was found but cannot be"
    echo "converted to new group $GROUPNAME because $GROUPNAME already exists."
    echo "Please delete one of the groups and then restart the installation."
    exit 1 ;
fi
# -- Both admin groups exist.
if [ "X$GPVSWADMEXISTS" != "X" ] && [ "X$LEGACY_ADM_GRP_FOUND" != "X" ] ; then
    echo "ERROR: The legacy group $LEGACY_ADMGROUPNAME was found but cannot be"
    echo "converted to new group $ADMGROUPNAME because $ADMGROUPNAME already exists."
    echo "Please delete one of the groups and then restart the installation."
    exit 1 ;
fi
echo

echo "+++++ Setting up $GROUPNAME group and admgroup..."
if [ "X$GPVSWEXISTS" != "X" ] ; then
    echo "An existing group $GROUPNAME was found."
else
    # PSQL-6827: If legacy group found then change just group name.
    if [ "X$LEGACY_PVSW_GRP_FOUND" != "X" ] ; then
        echo "Changing legacy group $LEGACY_GROUPNAME to $GROUPNAME..."

        if [ $(uname -s) = "Darwin" ]
        then
            dscl . -change /Groups/"$LEGACY_GROUPNAME" RecordName "$LEGACY_GROUPNAME" "$GROUPNAME"
        else
            groupmod -n $GROUPNAME $LEGACY_GROUPNAME
        fi
    else
        echo "Creating group $GROUPNAME..."
        if [ $(uname -s) = "Darwin" ]
        then
            groupid="$(($(dscl . -list /Groups PrimaryGroupID | awk '{print $2}' | sort -n | tail -n 1) + 1))"
            dscl . -create /Groups/"$GROUPNAME" PrimaryGroupID "$groupid"
        else
            /usr/sbin/groupadd $GRPFLAG $GROUPNAME
        fi
    fi
fi

if [ "X$GPVSWEXISTS" != "X" ] ; then
    echo "An existing group $ADMGROUPNAME was found."
else
    # PSQL-6827: If legacy admin group found then just change group name.
    if [ "X$LEGACY_ADM_GRP_FOUND" != "X" ] ; then
        echo "Changing legacy group $LEGACY_ADMGROUPNAME to $ADMGROUPNAME..."

        if [ $(uname -s) = "Darwin" ]
        then
            dscl . -change /Groups/"$LEGACY_ADMGROUPNAME" RecordName "$LEGACY_ADMGROUPNAME" "$ADMGROUPNAME"
        else
            groupmod -n $ADMGROUPNAME $LEGACY_ADMGROUPNAME
        fi
    else
        echo "Creating group $ADMGROUPNAME..."
        if [ $(uname -s) = "Darwin" ]
        then
            dscl . -delete /Groups/"$ADMGROUPNAME" 2>/dev/null
            dscl . -create /Groups/"$ADMGROUPNAME" PrimaryGroupID "$(($(dscl . -list /Groups PrimaryGroupID | awk '{print $2}' | sort -n | tail -n 1) + 1))"
        else
            /usr/sbin/groupadd $ADMGRPFLAG $ADMGROUPNAME
        fi
    fi
fi
echo


# Creating the root and bin directories if they don't exist
if [ ! -d "$ACTIANZEN_ROOT" ] ; then
    mkdir $ACTIANZEN_ROOT
fi
if [ ! -d "$ACTIANZEN_ROOT/bin" ] ; then
    mkdir $ACTIANZEN_ROOT/bin
fi

echo "+++++ Setting up user $USERNAME..."
if [ "X$UPVSWEXISTS" != "X" ] ; then
    echo "An existing user $USERNAME was found."
    
    # check if there are any problems with the existing user
    if [ $(uname -s) = "Darwin" ]
    then
        CPVSWSHELL=`dscl . -list /Users UserShell | awk '($1 == "'"$USERNAME"'")' | grep $USERSHL`
    else
        CPVSWSHELL=`echo $UPVSWEXISTS|grep $USERSHL`
    fi

    if [ "X$CPVSWSHELL" = "X" ] ; then
        echo "ERROR: Existing user $USERNAME has shell other than $USERSHL."
        exit 1 ;
    fi
else
    # PSQL-6827: If legacy user found then just change user name and update home directory.
    if [ "X$LEGACY_PUSER_FOUND" != "X" ] ; then
        echo "Changing legacy user $LEGACY_USERNAME to $USERNAME and updating users home directory..."

        if [ $(uname -s) = "Darwin" ]
        then
            dscl . -change /Users/"$LEGACY_USERNAME" RecordName "$LEGACY_USERNAME" "$USERNAME"
            mkdir -p "/Users/$USERNAME"
            chown -Rf $USERNAME:$GROUPNAME "/Users/$USERNAME"
            dscl . -change /Users/"$USERNAME" NFSHomeDirectory "/Users/$LEGACY_USERNAME" "/Users/$USERNAME"
        else
            NEWHOMEDIR=/home/$USERNAME
            usermod -l $USERNAME $LEGACY_USERNAME
            mkdir -p $NEWHOMEDIR
            chown -Rf $USERNAME:$GROUPNAME $NEWHOMEDIR
            usermod -d /home/$USERNAME $USERNAME
        fi
    else
        echo "Creating user $USERNAME"

        if [ $(uname -s) = "Darwin" ]
        then
            HOMEDIR=/Users/"$USERNAME"
            dscl . -create "$HOMEDIR" UniqueID "$(($(dscl . -list /Users UniqueID | awk '{print $2}' | sort -n | tail -n 1) + 1))"
            dscl . -create "$HOMEDIR" RealName "ActianZen"
            dscl . -create "$HOMEDIR" UserShell "$USERSHL"
            dscl . -create "$HOMEDIR" PrimaryGroupID "$(dscl . -list /Groups PrimaryGroupID | awk '($1 == "'"$GROUPNAME"'") { print $2 }')"
            dscl . -create "$HOMEDIR" NFSHomeDirectory "$HOMEDIR"
            defaults write /Library/Preferences/com.apple.loginwindow HiddenUsersList -array-add "$USERNAME"
            mkdir -p "$HOMEDIR"
            chown $USERNAME:$GROUPNAME "$HOMEDIR"
            chmod 755 "$HOMEDIR"
            unset HOMEDIR
        else
            /usr/sbin/useradd -c "ActianZen" -m -g $GROUPNAME -s $USERSHL $USERNAME
        fi

        if [ "$?" != "0" ]; then
            echo "ERROR: The user $USERNAME could not be created."
            exit 1;
        fi
        sleep 2

        TEMPFILE=/tmp/actianzen.tmp
        touch $TEMPFILE
        chown $USERNAME $TEMPFILE
        if [ "$?" != "0" ]; then
            echo "ERROR: The user $USERNAME could not be created."
            exit 1;
        fi
        rm -f $TEMPFILE
    fi
fi

echo "Configuring user $USERNAME environment..."
if [ $(uname -s) = "Darwin" ]
then
    HOMEDIR=/Users/"$USERNAME"
else
    # If there's an xhost command in our path.
    if which xhost >/dev/null 2>&1
    then
        if [ -f $ACTIANZEN_ROOT/bin/zencc ] ; then
            xhost local:$USERNAME
        fi
    fi

    HOMEDIR=`grep "^$USERNAME:" /etc/passwd | cut -d: -f6`
fi

PROFILE1=$HOMEDIR/$USERSHLPROFILE1
PROFILE2=$HOMEDIR/$USERSHLPROFILE2
PROFILE3=$HOMEDIR/$USERSHLPROFILE3

if [ $(uname -s) = "Darwin" ]
then
    if [ "$LANG" = "" ]
    then
        LANG="en_US.UTF-8"
    fi

    echo "User \"$USERNAME\" LANG environment variable will be set to \"$LANG\"."
fi

cat > $PROFILE1 << EOPF
umask 022
export ACTIANZEN_ROOT=$ACTIANZEN_ROOT
export PATH=\$ACTIANZEN_ROOT/bin:/bin:/usr/bin

if [ \$(uname -s) = "Darwin" ]
then
    export DYLD_LIBRARY_PATH=\$ACTIANZEN_ROOT/lib64:\$ACTIANZEN_ROOT/bin:/usr/lib
    export LANG=$LANG
else
    export LD_LIBRARY_PATH=\$ACTIANZEN_ROOT/lib:\$ACTIANZEN_ROOT/lib64:\$ACTIANZEN_ROOT/bin:/usr/lib:/usr/lib64
fi

export MANPATH=\$ACTIANZEN_ROOT/man:\$MANPATH
export BREQ=\$ACTIANZEN_ROOT/lib
export LD_BIND_NOW=1

EOPF

cp -f $PROFILE1 $PROFILE2
if [ ! -f $PROFILE3 ] ; then
cat > $PROFILE3 <<EOPF2
setenv ACTIANZEN_ROOT $ACTIANZEN_ROOT

if [ \$(uname -s) = "Darwin" ]
then
    setenv DYLD_LIBRARY_PATH \$ACTIANZEN_ROOT/lib64:\$DYLD_LIBRARY_PATH
    setenv LANG $LANG
else
    setenv LD_LIBRARY_PATH \$ACTIANZEN_ROOT/lib:\$ACTIANZEN_ROOT/lib64:\$LD_LIBRARY_PATH
fi

setenv MANPATH \$ACTIANZEN_ROOT/man:\$MANPATH
set path = (\$ACTIANZEN_ROOT/bin $path)
setenv BREQ \$ACTIANZEN_ROOT/lib
setenv LD_BIND_NOW 1

EOPF2
fi
echo


echo "+++++ Setting up symbolic links for libraries..."

# These used to be in the RPM spec file, but we moved them
# here for tarball installs.
rm -f $ACTIANZEN_ROOT/lib/libbtrvif.${SO_EXTENSION}

if [ ! $(uname -s) = "Darwin" ]
then
    # Use ldconfig to create links to major versions
    # selinux: ldconfig runs in its own domain and may not have
    # permissions to write to tty.
    $LDCONFIG -n $ACTIANZEN_ROOT/lib 2>&1 | cat
    if [ -d "$ACTIANZEN_ROOT/lib64" ]; then
        $LDCONFIG -n $ACTIANZEN_ROOT/lib64 2>&1 | cat
    fi

    MIF_FILE=`ls -1 $ACTIANZEN_ROOT/lib/libpsqlmif.${SO_EXTENSION}.?? 2> /dev/null | tail -1`
    if [ "x$MIF_FILE" = "x" ]; then
        MIF_FILE=`ls -1 $ACTIANZEN_ROOT/lib64/libpsqlmif.${SO_EXTENSION}.?? 2> /dev/null | tail -1`
    fi
    PSCL_FILE=`ls -1 $ACTIANZEN_ROOT/lib/libpscl.${SO_EXTENSION}.? 2> /dev/null | tail -1`
    if [ "x$PSCL_FILE" = "x" ]; then
        PSCL_FILE=`ls -1 $ACTIANZEN_ROOT/lib64/libpscl.${SO_EXTENSION}.? 2> /dev/null | tail -1`
    fi
    PSQL_VER=`echo $MIF_FILE | sed -e 's/.*[.]\(.*\)/\1/'`
    PS_VER=$PSQL_VER

    dso_symlink () {
        dso=$1
        ver=$2
        if [ -f $ACTIANZEN_ROOT/lib/$dso.$ver ]; then
            (cd $ACTIANZEN_ROOT/lib; ln -sf $dso.$ver $dso )
        fi
        if [ -f $ACTIANZEN_ROOT/lib64/$dso.$ver ]; then
            (cd $ACTIANZEN_ROOT/lib64; ln -sf $dso.$ver $dso )
        fi
    }
else
    MIF_FILE=`ls -1 $ACTIANZEN_ROOT/lib64/libpsqlmif.${SO_EXTENSION}.[0-9]* 2> /dev/null | tail -1`
    if [ "x$MIF_FILE" = "x" ]; then
        MIF_FILE=`ls -1 $ACTIANZEN_ROOT/lib64/libpsqlmif.${SO_EXTENSION}.[0-9]* 2> /dev/null | tail -1`
    fi
    PSQL_VER=`echo $MIF_FILE | sed -e 's/^.*\.'"${SO_EXTENSION}"'\.\([0-9]*\)\..*$/\1/'`
    PS_VER=$PSQL_VER

    dso_symlink () {
      dso=$1
      ver=$2
      if [ -f $ACTIANZEN_ROOT/lib64/$dso.$ver.[0-9]*.* ]; then
          (cd $ACTIANZEN_ROOT/lib64; ln -sf $dso.$ver.[0-9]*.* $dso.$ver ; ln -sf $dso.$ver $dso )
      fi
    }

    dso_symlink libbdulb.${SO_EXTENSION} $PSQL_VER
    dso_symlink libclientrb.${SO_EXTENSION} $PSQL_VER
    dso_symlink libcobolschemaexecmsgrb.${SO_EXTENSION} $PSQL_VER
    dso_symlink libdbcsipxy.${SO_EXTENSION} $PSQL_VER
    dso_symlink libdcm100.${SO_EXTENSION} $PSQL_VER
    dso_symlink libenginelm.${SO_EXTENSION} $PSQL_VER
    dso_symlink libexp010.${SO_EXTENSION} $PSQL_VER
    dso_symlink liblegacylm.${SO_EXTENSION} $PSQL_VER
    dso_symlink liblicmgrrb.${SO_EXTENSION} $PSQL_VER
    dso_symlink libmkc3.${SO_EXTENSION} $PSQL_VER
    dso_symlink libmkderb.${SO_EXTENSION} $PSQL_VER
    dso_symlink libodbccr.${SO_EXTENSION} 1
    dso_symlink libpceurop.${SO_EXTENSION} $PS_VER
    dso_symlink libpctlgrb.${SO_EXTENSION} $PSQL_VER
    dso_symlink libpscl.${SO_EXTENSION} $PS_VER
    dso_symlink libpscore.${SO_EXTENSION} $PS_VER
    dso_symlink libpscp932.${SO_EXTENSION} $PS_VER
    dso_symlink libpsdom.${SO_EXTENSION} $PS_VER
    dso_symlink libpseucjp.${SO_EXTENSION} $PS_VER
    dso_symlink libpssax.${SO_EXTENSION} $PS_VER
    dso_symlink libpsutilrb.${SO_EXTENSION} $PS_VER
    dso_symlink libpvmsgrb.${SO_EXTENSION} $PSQL_VER
    dso_symlink libsrderb.${SO_EXTENSION} $PSQL_VER
    dso_symlink libupiapirb.${SO_EXTENSION} $PSQL_VER
    dso_symlink libxlate.${SO_EXTENSION} $PSQL_VER
fi

dso_symlink libodbc.${SO_EXTENSION} 1
dso_symlink libpsqldti.${SO_EXTENSION} $PSQL_VER
dso_symlink libclientlm.${SO_EXTENSION} $PSQL_VER

dso_symlink libels.${SO_EXTENSION} '*.*.*.*'

dso_symlink libbtrieveC.${SO_EXTENSION} $PSQL_VER
dso_symlink libbtrieveCpp.${SO_EXTENSION} $PSQL_VER
dso_symlink libpsqlmif.${SO_EXTENSION} $PSQL_VER
dso_symlink libpsqlnsl.${SO_EXTENSION} $PSQL_VER
dso_symlink libpsqlcsm.${SO_EXTENSION} $PSQL_VER
dso_symlink libpsqlcsp.${SO_EXTENSION} $PSQL_VER
dso_symlink libodbcci.${SO_EXTENSION} $PSQL_VER
dso_symlink libcsi100.${SO_EXTENSION} $PSQL_VER

echo

cd $ACTIANZEN_ROOT/bin 
if [ -f $ACTIANZEN_ROOT/bin/psregedit64 ] ; then
    ln -sf psregedit64 psregedit
fi
if [ -f $ACTIANZEN_ROOT/bin/clilcadm64 ] ; then
    ln -sf clilcadm64 clilcadm
fi
if [ -f $ACTIANZEN_ROOT/bin/licgetauth64 ] ; then
    ln -sf licgetauth64 licgetauth
fi
if [ -f $ACTIANZEN_ROOT/bin/isql64 ] ; then
    ln -sf isql64 isql
fi
cd $ACTIANZEN_ROOT

if [ -x $SELINUX_ENABLED ] && $SELINUX_ENABLED; then
    /sbin/restorecon -R $ACTIANZEN_ROOT
    if [ -d "$ACTIANZEN_ROOT"/lib64 ]
    then
        /usr/bin/chcon -t textrel_shlib_t "$ACTIANZEN_ROOT"/lib64/*.so "$ACTIANZEN_ROOT"/lib64/*.so.*
    fi
fi

echo "+++++ Making user $USERNAME owner of all files in $ACTIANZEN_ROOT..."

chown -Rf $USERNAME:$GROUPNAME $ACTIANZEN_ROOT
chmod g+w $ACTIANZEN_ROOT/etc
# install-related scripts remain owned by root so zen cannot uninstall (which fails)
if [ $(uname -s) = "Darwin" ]
then
    chown root:wheel $ACTIANZEN_ROOT/etc/*install.sh
else
    chown root:root $ACTIANZEN_ROOT/etc/*install.sh
fi
chmod 770 $ACTIANZEN_ROOT/etc/*install.sh
echo


echo "+++++ Registering the PCOM libraries..."

# Sym links must already exist
dso_register () {
    dso=$1
    ver=$2
    if [ -f $ACTIANZEN_ROOT/lib/$dso.$ver ]; then
        $ACTIANZEN_ROOT/bin/psregsvr $ACTIANZEN_ROOT/lib/$dso.$ver
        if [ $? -ne 0 ]; then
            echo ERROR: PCOM registration failed for $dso.$ver.
            exit 1;
        fi
    fi
    if [ -f $ACTIANZEN_ROOT/lib64/$dso.$ver ]; then
        $ACTIANZEN_ROOT/bin/psregsvr64 $ACTIANZEN_ROOT/lib64/$dso.$ver
        if [ $? -ne 0 ]; then
            echo ERROR: PCOM registration failed for $dso.$ver.
            exit 1;
        fi
    fi
}
dso_register libpctlgrb.${SO_EXTENSION} $PSQL_VER
dso_register libcobolschemaexecmsgrb.${SO_EXTENSION} $PSQL_VER
dso_register libpceurop.${SO_EXTENSION} $PS_VER
dso_register libpssax.${SO_EXTENSION} $PS_VER
dso_register libpsdom.${SO_EXTENSION} $PS_VER
dso_register libpsutilrb.${SO_EXTENSION} $PS_VER
dso_register libclientrb.${SO_EXTENSION} $PSQL_VER
dso_register libupiapirb.${SO_EXTENSION} $PSQL_VER
dso_register libcsi100.${SO_EXTENSION} $PSQL_VER
dso_register libmkc3.${SO_EXTENSION} $PSQL_VER
dso_register libpvmsgrb.${SO_EXTENSION} $PSQL_VER
dso_register libpsqlcsm.${SO_EXTENSION} $PSQL_VER
dso_register libpsqlcsp.${SO_EXTENSION} $PSQL_VER
dso_register libdbcsipxy.${SO_EXTENSION} $PSQL_VER
dso_register libpsqlmpm.${SO_EXTENSION} $PSQL_VER
dso_register libpscp932.${SO_EXTENSION} $PS_VER
dso_register libsrderb.${SO_EXTENSION} $PSQL_VER

# Reset owner to zen-svc:zen-data
PS_REGISTRY=$ACTIANZEN_ROOT/etc/.PSRegistry
PS_SEM_IPC_SEM_ID="/tmp/PS_sem_*_IPC_SEM.id"
PS_SHM_IPC_SHARED_MEM_ID="/tmp/PS_shm_*_IPC_SHARED_MEM.id"

# If PS_REGISTRY exists.
if [ -e $PS_REGISTRY ] ; then
    chown -Rf $USERNAME:$GROUPNAME $PS_REGISTRY

	# If there are any PS_SEM_IPC_SEM_ID files.
	if ls $PS_SEM_IPC_SEM_ID > /dev/null 2>&1 ; then
		chown -f $USERNAME:$GROUPNAME $PS_SEM_IPC_SEM_ID
	fi

	# If there are any PS_SHM_IPC_SHARED_MEM_ID files.
	if ls $PS_SHM_IPC_SHARED_MEM_ID > /dev/null 2>&1 ; then
		chown -f $USERNAME:$GROUPNAME $PS_SHM_IPC_SHARED_MEM_ID
	fi

    # Add read only for everybody and full control for user and group.
    chmod -R a-rwx  $PS_REGISTRY
    chmod -R a+rx   $PS_REGISTRY
    chmod -R ug+rwx $PS_REGISTRY
fi


################# Creating the DSN ini file ######################
ODBCINI=$ACTIANZEN_ROOT/etc/odbc.ini
if [ ! -f $ODBCINI ] ; then
    touch $ODBCINI
    chown $USERNAME:$GROUPNAME $ODBCINI
else
    echo "An existing DSN $DSNNAME was found."
fi
chmod a-wrx $ODBCINI
chmod a+r $ODBCINI
chmod ug+w $ODBCINI

if [ -f $ACTIANZEN_ROOT/bin/dsnadd ] ; then
    chmod a+x $ACTIANZEN_ROOT/bin/dsnadd
fi

# Populate the InstallInfo registry settings.
MIF_FILE=`ls -1 $ACTIANZEN_ROOT/lib/libpsqlmif.${SO_EXTENSION}.*.*.* 2> /dev/null | tail -1`
if [ "x$MIF_FILE" = "x" ]; then
    MIF_FILE=`ls -1 $ACTIANZEN_ROOT/lib64/libpsqlmif.${SO_EXTENSION}.*.*.* 2> /dev/null | tail -1`
fi
PRODUCT_VER=`egrep 'ProductVer=?' $ACTIANZEN_ROOT/etc/postinstall.cfg | cut -d= -f2`
VERSION_LEVEL=`egrep 'VersionLevel=?' $ACTIANZEN_ROOT/etc/postinstall.cfg | cut -d= -f2`

PSREGEDIT=psregedit
if [ -f $ACTIANZEN_ROOT/bin/psregedit64 ] ; then
    PSREGEDIT=psregedit64
fi
$PSREGEDIT -set -key "PS_HKEY_CONFIG/SOFTWARE/Actian/Zen/InstallInfo/Client" -value VersionLevel $VERSION_LEVEL
$PSREGEDIT -set -key "PS_HKEY_CONFIG/SOFTWARE/Actian/Zen/InstallInfo/Client" -value InstallDir "$ACTIANZEN_ROOT"
$PSREGEDIT -set -key "PS_HKEY_CONFIG/SOFTWARE/Actian/Zen/InstallInfo/Client" -value InstallData "$ACTIANZEN_ROOT"
$PSREGEDIT -set -key "PS_HKEY_CONFIG/SOFTWARE/Actian/Zen/InstallInfo/Client" -value ProductVersion $PRODUCT_VER
chown -Rf $USERNAME:$GROUPNAME $PS_REGISTRY

# PSQL-6969: If v13 or earlier previously uninstalled, export the legacy registry to registry_export_pre-zen.txt.
# Note psregedit recognizes ACTIANZEN_ROOT so we need to temporarily change it to the legacy PVSW_ROOT value.
if [ -e "$PVSW_ROOT/etc/.PSRegistry" ] ; then
   echo "Exporting legacy registry to $ACTIANZEN_ROOT/etc/registry_export_pre-zen.txt"
   TEMP_PATH=$ACTIANZEN_ROOT
   ACTIANZEN_ROOT=$PVSW_ROOT $PSREGEDIT -export -key "PS_HKEY_CONFIG/SOFTWARE/Pervasive Software" -file $TEMP_PATH/etc/registry_export_pre-zen.txt
   chown -f $USERNAME:$GROUPNAME $TEMP_PATH/etc/registry_export_pre-zen.txt
   chmod 664 $TEMP_PATH/etc/registry_export_pre-zen.txt
fi

# PSQL-6969: If v13 or earlier previously uninstalled, copy content from legacy odbc.ini,
# updating driver paths to new location.
ACTIANZEN_ODBCINI=$ACTIANZEN_ROOT/etc/odbc.ini
LEGACY_ODBCINI=$PVSW_ROOT/etc/odbc.ini
if [ -e "$LEGACY_ODBCINI.rpmsave" ] ; then
   if [ -f $ACTIANZEN_ODBCINI ] ; then
      echo "Archiving $ACTIANZEN_ODBCINI to $ACTIANZEN_ODBCINI.org"
      mv $ACTIANZEN_ODBCINI $ACTIANZEN_ODBCINI.org
   fi
   echo "Restoring the odbc configuration from legacy install..."
   sed -e '/^Driver/s/psql/actianzen/' $LEGACY_ODBCINI.rpmsave > $ACTIANZEN_ODBCINI
   chown -f $USERNAME:$GROUPNAME $ACTIANZEN_ODBCINI
fi
if [ -e "$LEGACY_ODBCINI" ] ; then
   if [ -f $ACTIANZEN_ODBCINI ] ; then
      echo "Archiving $ACTIANZEN_ODBCINI to $ACTIANZEN_ODBCINI.org"
      mv $ACTIANZEN_ODBCINI $ACTIANZEN_ODBCINI.org
   fi
   echo "Restoring the odbc configuration from legacy install..."
   sed -e '/^Driver/s/psql/actianzen/' $LEGACY_ODBCINI > $ACTIANZEN_ODBCINI
   chown -f $USERNAME:$GROUPNAME $ACTIANZEN_ODBCINI
fi

echo

echo "++++ Restoring the configuration from previous install..."
ETCDIR=$ACTIANZEN_ROOT/etc
SAVESUFFIX=rpmsave
SAVEPKGSUFFIX=pkgsave
# SAVEPKGSUFFIX is to save the file when tarball install/uninstall is done
if [ ! -f $ETCDIR/$cfgfile.$SAVESUFFIX ] ; then
    for cfgfile in odbc.ini ; do
        if [ -f $ETCDIR/$cfgfile.$SAVEPKGSUFFIX ] ; then
            if [ -f $ETCDIR/$cfgfile ] ; then
                echo "Archiving $cfgfile to $ETCDIR/$cfgfile.org"
                mv $ETCDIR/$cfgfile $ETCDIR/$cfgfile.org
            fi
            echo "Restoring previous $cfgfile.$SAVEPKGSUFFIX to $ETCDIR/$cfgfile"
            mv $ETCDIR/$cfgfile.$SAVEPKGSUFFIX $ETCDIR/$cfgfile
        fi
    done
else
    for cfgfile in odbc.ini ; do
        if [ -f $ETCDIR/$cfgfile.$SAVESUFFIX ] ; then
            if [ -f $ETCDIR/$cfgfile ] ; then
                echo "Archiving $cfgfile to $ETCDIR/$cfgfile.org"
                mv $ETCDIR/$cfgfile $ETCDIR/$cfgfile.org
            fi
            echo "Restoring previous $cfgfile.$SAVESUFFIX to $ETCDIR/$cfgfile"
            mv $ETCDIR/$cfgfile.$SAVESUFFIX $ETCDIR/$cfgfile
        fi
    done
fi
chmod a-wrx $ODBCINI
chmod a+r $ODBCINI
chmod ug+w $ODBCINI
rm -f $ETCDIR/psql
echo


# Control Center, Builder and Docs configurations
#
# BEGIN CHECK FOR 32-bit gtk2 (Required for Control Center)
#
GOT_GTK=1;

if [ -f $ACTIANZEN_ROOT/bin/zencc -o -f $ACTIANZEN_ROOT/bin/builder ] ; then
    if [ -f $ACTIANZEN_ROOT/bin/zencc ] ; then
        echo Initializing Control Center...

        # If this is Darwin.
        if [ $(uname -s) = "Darwin" ]
        then
            $ACTIANZEN_ROOT/bin/Zen\ Control\ Center.app/Contents/MacOS/zencc -clean -initialize
        else
            echo "$ACTIANZEN_ROOT/bin/zencc -clean -initialize" | $SU $USERNAME $SU_ARGS
        fi

        echo 
    fi
    if [ -f $ACTIANZEN_ROOT/bin/builder ] ; then
        echo Initializing builder...

        # If this is Darwin.
        if [ $(uname -s) = "Darwin" ]
        then
            $ACTIANZEN_ROOT/bin/Zen\ DDF\ Builder.app/Contents/MacOS/builder -clean -initialize
        else
            echo "$ACTIANZEN_ROOT/bin/builder -clean -initialize" | $SU $USERNAME $SU_ARGS
        fi

        echo 
    fi
fi

if [ -d "$ACTIANZEN_ROOT/bin/configuration" ] ; then
    rm -Rf $ACTIANZEN_ROOT/bin/configuration/org.eclipse.help.base/index
    chmod 777 -Rf $ACTIANZEN_ROOT/bin/configuration 2>/dev/null
    chown -Rf $USERNAME:$GROUPNAME $ACTIANZEN_ROOT/bin/configuration
fi
if [ -d "$ACTIANZEN_ROOT/docs" ] ; then
    chmod -R 555 $ACTIANZEN_ROOT/docs
fi
PSQLHOME=`$SU $USERNAME -c "echo ~"`
chown -Rf $USERNAME:$GROUPNAME $PSQLHOME

# If this is Darwin.
if [ $(uname -s) = "Darwin" ]
then
    APPLICATIONS="/Applications"

    # If there is an applications directory.
    if [ -d "$APPLICATIONS" ]
    then
        ACTIAN_ZEN_14="$APPLICATIONS/Actian Zen 14"
        UTILITIES="$ACTIAN_ZEN_14/Utilities"
        echo "+++++ Populating the \"$ACTIAN_ZEN_14\" directory...."

        # If all of this succeeds.
        if (test -d "$ACTIAN_ZEN_14" || mkdir "$ACTIAN_ZEN_14") && \
            chmod 755 "$ACTIAN_ZEN_14" && \
            (test -d "$UTILITIES" || mkdir "$UTILITIES") && \
            chmod 755 "$UTILITIES" && \
            cp -r "$ACTIANZEN_ROOT/bin/scripts/Zen Uninstall.app" "$ACTIAN_ZEN_14" && \
            chmod -R 755 "$ACTIAN_ZEN_14/Zen Uninstall.app" && \
            cp "$ACTIANZEN_ROOT/bin/scripts/messages" "$ACTIAN_ZEN_14/.messages" && \
            chmod 644 "$ACTIAN_ZEN_14/.messages" && \
            cp "$ACTIANZEN_ROOT/bin/scripts/uninstall" "$ACTIAN_ZEN_14/.uninstall" && \
            chmod 755 "$ACTIAN_ZEN_14/.uninstall" && \
            cp -r "$ACTIANZEN_ROOT/bin/Zen Control Center.app" "$ACTIAN_ZEN_14" && \
            chmod -R 755 "$ACTIAN_ZEN_14/Zen Control Center.app" && \
            cp -r "$ACTIANZEN_ROOT/bin/Zen DDF Builder.app" "$UTILITIES" && \
            chmod -R 755 "$UTILITIES/Zen DDF Builder.app"
        then
            :
        else
            echo "Warning: Unable to correctly populate the \"$ACTIAN_ZEN_14\" directory."
        fi

        echo
    fi
fi

if [ -f $ACTIANZEN_ROOT/docs/readme_zen*.htm ] ; then
echo View the README file located at:
ls $ACTIANZEN_ROOT/docs/readme_zen*.htm
echo
fi
if [ -f $ACTIANZEN_ROOT/bin/plugins/com.pervasive.psql.docs_1.0.0.jar ] ; then
echo From the Zen Control Center, you may browse the complete Zen documentation by running:
echo "    $ACTIANZEN_ROOT/bin/zencc"
echo
fi

echo Install has successfully completed.

echo
