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

echo "+++++ Removing Zen directories..."
####################################################################
#  Note: Any changes to the files to be removed must be made to the 
#        corresponding Zen-Clients-Linux.spec file %postun section. 
####################################################################
HOMEDIR=`grep "^zen-svc:" /etc/passwd | cut -d: -f6`
rm -f $HOMEDIR/.cshrc
rm -f $HOMEDIR/.bash_profile
rm -f $HOMEDIR/.bash_history
rm -f $HOMEDIR/.bashrc
rm -f  $ACTIANZEN_ROOT/LICENSE
rm -f  $ACTIANZEN_ROOT/Zen_Third_Party_Notice
rm -f  $ACTIANZEN_ROOT/etc/client*.sh
rm -f  $ACTIANZEN_ROOT/etc/postinstall.cfg
rm -f  $ACTIANZEN_ROOT/etc/psql-unixODBC-2.2.11.tar.gz
rm -Rf $ACTIANZEN_ROOT/bin
rm -Rf $ACTIANZEN_ROOT/jre
rm -Rf $ACTIANZEN_ROOT/lib
rm -Rf $ACTIANZEN_ROOT/lib64
rm -Rf $ACTIANZEN_ROOT/man
rm -Rf $ACTIANZEN_ROOT/docs
rm -Rf $ACTIANZEN_ROOT/log
echo ""

# If this is Darwin.
if [ $(uname -s) = "Darwin" ]
then
    APPLICATIONS="/Applications"

    # If there is an applications directory.
    if [ -d "$APPLICATIONS" ]
    then
        ACTIAN_ZEN_14="$APPLICATIONS/Actian Zen 14"
        UTILITIES="$ACTIAN_ZEN_14/Utilities"
        echo "+++++ Unpopulating the \"$ACTIAN_ZEN_14\" directory...."
        ERROR=0

        # If removing "Zen DDF Builder.app" fails.
        if ! rm -r "$UTILITIES/Zen DDF Builder.app"
        then
            ERROR=1
        fi

        # If removing ".DS_Store" fails.
        if ! rm -f "$UTILITIES/.DS_Store"
        then
            ERROR=1
        fi

        # If removing "$UTILITIES" fails.
        if ! rmdir "$UTILITIES"
        then
            ERROR=1
        fi

        # If removing "Zen Control Center.app" fails.
        if ! rm -r "$ACTIAN_ZEN_14/Zen Control Center.app"
        then
            ERROR=1
        fi

        # If removing "Zen Uninstall.app" fails.
        if ! rm -r "$ACTIAN_ZEN_14/Zen Uninstall.app" || ! rm "$ACTIAN_ZEN_14/.uninstall"
        then
            ERROR=1
        fi

        # If removing "messages" fails.
        if ! rm "$ACTIAN_ZEN_14/.messages"
        then
            ERROR=1
        fi

        # If removing ".DS_Store" fails.
        if ! rm -f "$ACTIAN_ZEN_14/.DS_Store"
        then
            ERROR=1
        fi

        # If removing "$ACTIAN_ZEN_14" fails.
        if ! rmdir "$ACTIAN_ZEN_14"
        then
            ERROR=1
        fi

        # If there were any errors.
        if [ "$ERROR" -eq 1 ]
        then
            echo "Error: Unable to correctly unpopulate the \"$ACTIAN_ZEN_14\" directory."
            exit 1
        fi

        echo
    fi
fi

echo "Uninstall has successfully completed."
echo



















