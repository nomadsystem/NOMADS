for a in *.mp3; do
  b=$(printf droplets%03d.mp3 ${a%.mp3})
  if [ $a != $b ]; then
    mv $a $b
  fi
done