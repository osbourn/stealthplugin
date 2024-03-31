#!/usr/bin/env bash
set -e

echo 'Checking for java...'
if command -v java &> /dev/null
then
  echo 'Java is installed. Please make sure it is the correct version (17 and up).'
else
  echo 'Java not found, attempting to install it.'
  if command -v apt-get &> /dev/null
  then
    echo 'Using apt to install java'
    echo 'Running apt-get update'
    sudo apt-get update
    echo 'Running apt-get --assume-yes install openjdk-17-jdk'
    sudo apt-get --assume-yes install openjdk-17-jdk
    echo 'Installation finished.'
  elif command -v yum &> /dev/null
  then
    # For my purposes, this is most likely Amazon Linux.
    echo 'Using yum to install java'
    echo 'Running yum --assumeyes install java-17-amazon-corretto.x86_64'
    sudo yum --assumeyes install java-17-amazon-corretto.x86_64
    echo 'Installation finished.'
  else
    echo 'Error: Neither apt nor yum is installed. Please install java manually.'
    exit 1
  fi
fi

echo 'Making and entering ~/minecraft/ directory...'
cd ~
mkdir minecraft
cd minecraft

echo 'Downloading Paper...'
curl -O 'https://api.papermc.io/v2/projects/paper/versions/1.20.4/builds/463/downloads/paper-1.20.4-463.jar'
echo 'Paper downloaded.'

echo 'Creating start.sh'
cat << EOF > start.sh
#!/usr/bin/env bash
java -jar -Xms4G -Xmx6G paper-1.20.4-463.jar nogui
EOF
chmod +x start.sh

echo 'Preconfiguring server.properties with recommended values...'
cat << EOF > server.properties
spawn-protection=0
level-type=flat
generator-settings={"layers"\:[{"block"\:"minecraft\:air","height"\:1}],"biome"\:"minecraft\:the_void"}
enable-command-block=true
EOF

echo 'Running for the first time...'
bash start.sh

echo 'Accepting EULA...'
sed -i -e 's/eula=false/eula=true/' eula.txt

echo 'Downloading stealthplugin'
echo 'Making API call to find current version URL...'
DOWNLOAD_URL=$(curl https://api.github.com/repos/osbourn/stealthplugin/releases/latest |
  grep -o 'https://github.com/osbourn/stealthplugin/releases/download/.*/stealthplugin-.*\.jar')
cd plugins
echo "Downloading $DOWNLOAD_URL"
curl -O "$DOWNLOAD_URL"
cd ..

echo 'Downloading recommended plugins...'
cd plugins
curl -O 'https://cdn.modrinth.com/data/1u6JkXh5/versions/JzCMkGax/worldedit-bukkit-7.3.0.jar'
curl -O 'https://cdn.modrinth.com/data/9eGKb6K1/versions/WvPmSPnl/voicechat-bukkit-2.5.10.jar'
curl -O 'https://cdn.modrinth.com/data/MEPADOya/versions/wVSaDcGA/voicechat-interaction-paper-v1.3.1%2B1.20.2.jar'
curl -O 'https://cdn.modrinth.com/data/MubyTbnA/versions/Y9N0QwjG/FreedomChat-1.5.2.jar'
# Currently skipping ProtocolLib since it is in development for 1.20, although unstable dev versions do exist
cd ..
