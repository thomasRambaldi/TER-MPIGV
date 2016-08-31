#export KALDI_ROOT=../../../dictate/kaldi-rocio/

#export PATH=$PWD/utils/:$KALDI_ROOT/src/bin:$KALDI_ROOT/tools/openfst/bin:$KALDI_ROOT/src/fstbin/:$KALDI_ROOT/src/gmmbin/:$KALDI_ROOT/src/featbin/:$KALDI_ROOT/src/lm/:$KALDI_ROOT/src/sgmmbin/:$KALDI_ROOT/src/sgmm2bin/:$KALDI_ROOT/src/fgmmbin/:$KALDI_ROOT/src/latbin/:$KALDI_ROOT/src/nnet2bin:$PWD:$PATH:$KALDI_ROOT/tools/sph2pipe_v2.5/:$KALDI_ROOT/src/online2bin/:$KALDI_ROOT/src/ivectorbin/
#export LC_ALL=C

if [ `uname` == Darwin ]; then
	dir=$PWD/../../Darwin
else
	dir=$PWD/../
    alias greadlink=readlink
    alias gln=ln
fi
#export LD_LIBRARY_PATH=$dir/libs:$dir/libs/fst:$dir/libs/atlas
#export PATH=$dir/bin:$PATH
export PATH=/home/kevin/Programmation/TER/modified-kaldi/kaldi-rocio-master/src/bin:/home/kevin/Programmation/TER/modified-kaldi/kaldi-rocio-master/src/fstbin:/home/kevin/Programmation/TER/modified-kaldi/kaldi-rocio-master/src/lmbin:/home/kevin/Programmation/TER/modified-kaldi/kaldi-rocio-master/tools/openfst/bin:/home/kevin/Programmation/TER/mitlm-master:/home/kevin/Programmation/TER/CRF++-0.58:$PATH
