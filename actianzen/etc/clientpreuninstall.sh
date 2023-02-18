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
PATH=$ACTIANZEN_ROOT/bin:$PATH
if [ $(uname -s) = "Darwin" ]
then
    DYLD_LIBRARY_PATH=$ACTIANZEN_ROOT/bin:$ACTIANZEN_ROOT/lib:$ACTIANZEN_ROOT/lib64:$DYLD_LIBRARY_PATH
    export DYLD_LIBRARY_PATH
    SO_EXTENSION="dylib"
else
    LD_LIBRARY_PATH=$ACTIANZEN_ROOT/bin:$ACTIANZEN_ROOT/lib:$ACTIANZEN_ROOT/lib64:$LD_LIBRARY_PATH
    export LD_LIBRARY_PATH
    SO_EXTENSION="so"
fi
export ACTIANZEN_ROOT
USERNAME=zen-svc

if [ $(uname -s) = "Darwin" ]
then
    MIF_FILE=`ls -1 $ACTIANZEN_ROOT/lib/libpsqlmif.${SO_EXTENSION}.* 2> /dev/null | tail -1`
    if [ "x$MIF_FILE" = "x" ]; then
        MIF_FILE=`ls -1 $ACTIANZEN_ROOT/lib/libpsqlmif.${SO_EXTENSION}.* 2> /dev/null | tail -1`
    fi
    PSQL_VER=`echo $MIF_FILE | sed -e 's/^.*\.dylib\.//'`
    PS_VER=$PSQL_VER
else
    MIF_FILE=`ls -1 $ACTIANZEN_ROOT/lib64/libpsqlmif.${SO_EXTENSION}.?? 2> /dev/null | tail -1`
    PSQL_VER=`echo $MIF_FILE | sed -e 's/.*[.]\(.*\)/\1/'`
    PS_VER=$PSQL_VER
fi

echo +++++ Unregistering the PCOM libraries...

dso_unregister () {
    dso=$1
    if [ -f $ACTIANZEN_ROOT/lib/$dso ]; then
        if [ -f $ACTIANZEN_ROOT/bin/psregsvr ]; then
            $ACTIANZEN_ROOT/bin/psregsvr -u $ACTIANZEN_ROOT/lib/$dso
        fi
    fi
    if [ -f $ACTIANZEN_ROOT/lib64/$dso ]; then
        $ACTIANZEN_ROOT/bin/psregsvr64 -u $ACTIANZEN_ROOT/lib64/$dso
    fi
}
dso_unregister libpctlgrb.${SO_EXTENSION}.$PSQL_VER
dso_unregister libcobolschemaexecmsgrb.${SO_EXTENSION}.$PSQL_VER
dso_unregister libpceurop.${SO_EXTENSION}.$PS_VER
dso_unregister libpssax.${SO_EXTENSION}.$PS_VER
dso_unregister libpsdom.${SO_EXTENSION}.$PS_VER
dso_unregister libpsutilrb.${SO_EXTENSION}.$PS_VER
dso_unregister libclientrb.${SO_EXTENSION}.$PSQL_VER
dso_unregister libupiapirb.${SO_EXTENSION}.$PSQL_VER
dso_unregister libcsi100.${SO_EXTENSION}.$PSQL_VER
dso_unregister libmkc3.${SO_EXTENSION}.$PSQL_VER
dso_unregister libpvmsgrb.${SO_EXTENSION}.$PSQL_VER
dso_unregister libpsqlcsm.${SO_EXTENSION}.$PSQL_VER
dso_unregister libpsqlcsp.${SO_EXTENSION}.$PSQL_VER
dso_unregister libdbcsipxy.${SO_EXTENSION}.$PSQL_VER
dso_unregister libpscp932.${SO_EXTENSION}.$PS_VER
dso_unregister libsrderb.${SO_EXTENSION}.$PSQL_VER
echo


echo "+++++ Removing symbolic links to libraries..."

dso_unsymlink () {
    dso=$1
    if [ -h $ACTIANZEN_ROOT/lib/$dso ]; then
        rm -f $ACTIANZEN_ROOT/lib/$dso
    fi
    if [ -h $ACTIANZEN_ROOT/lib64/$dso ]; then
        rm -f $ACTIANZEN_ROOT/lib64/$dso
    fi
}
rm -f $ACTIANZEN_ROOT/bin/odbcci.${SO_EXTENSION}
dso_unsymlink libbtrvif.${SO_EXTENSION}
dso_unsymlink libiodbc.${SO_EXTENSION}
dso_unsymlink libiodbc.${SO_EXTENSION}.2
dso_unsymlink libodbcci.${SO_EXTENSION}
dso_unsymlink odbcci.${SO_EXTENSION}
dso_unsymlink odbcci.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libodbc.${SO_EXTENSION}
dso_unsymlink libodbc.${SO_EXTENSION}.1
if [ $(uname -s) = "Darwin" ]
then
    dso_unsymlink libodbccr.${SO_EXTENSION}.1
fi
dso_unsymlink libodbcci.${SO_EXTENSION}
dso_unsymlink libodbcci.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libpscore.${SO_EXTENSION}.$PS_VER
dso_unsymlink libpscl.${SO_EXTENSION}.$PS_VER
dso_unsymlink libxlate.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libpsqldti.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libpsqldti.${SO_EXTENSION}
dso_unsymlink libclientlm.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libclientlm.${SO_EXTENSION}
dso_unsymlink libels.${SO_EXTENSION}
dso_unsymlink libbtrieveC.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libbtrieveC.${SO_EXTENSION}
dso_unsymlink libbtrieveCpp.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libbtrieveCpp.${SO_EXTENSION}
dso_unsymlink libpsqlmif.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libpsqlmif.${SO_EXTENSION}
dso_unsymlink libpsqlnsl.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libpsqlnsl.${SO_EXTENSION}
dso_unsymlink libpctlgrb.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libpctlgrb.${SO_EXTENSION}
dso_unsymlink libcobolschemaexecmsgrb.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libcobolschemaexecmsgrb.${SO_EXTENSION}
dso_unsymlink libpsutilrb.${SO_EXTENSION}.$PS_VER
dso_unsymlink libpceurop.${SO_EXTENSION}.$PS_VER
dso_unsymlink libpseucjp.${SO_EXTENSION}.$PS_VER
dso_unsymlink libpscp932.${SO_EXTENSION}.$PS_VER
dso_unsymlink libpssax.${SO_EXTENSION}.$PS_VER
dso_unsymlink libpsdom.${SO_EXTENSION}.$PS_VER
dso_unsymlink libclientrb.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libmkc3.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libpsqlmpm.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libupiapirb.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libpvmsgrb.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libpsqlcsm.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libpsqlcsm.${SO_EXTENSION}
dso_unsymlink libpsqlcsp.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libdbcsipxy.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libdbcsi100.${SO_EXTENSION}.$PSQL_VER
dso_unsymlink libsrderb.${SO_EXTENSION}
dso_unsymlink libsrderb.${SO_EXTENSION}.$PSQL_VER

echo


echo "+++++ Backing up $ACTIANZEN_ROOT/etc/odbc.ini..."

sync
cp -f $ACTIANZEN_ROOT/etc/odbc.ini $ACTIANZEN_ROOT/etc/odbc.ini.pkgsave
echo
