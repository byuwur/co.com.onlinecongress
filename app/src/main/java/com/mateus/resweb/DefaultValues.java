package com.mateus.resweb;

public class DefaultValues {

    public String url = "http://www.sistemas-i-computacion-tic.com/reserv/phone/";
    public String urlinicio = url+"inicio/";
    public String urlcanchas = url+"canchas/";
    public String urlponencia = url+"reservar/";
    public String urlconferencia = url+"reservas/";
    public String urlagendado = url+"favoritos/";
    public String urlcuenta = url+"cuenta/";
    public String urllistar = url+"listar/";
    public String imgcanchasurl = "http://www.sistemas-i-computacion-tic.com/reserv/admin/Imagenes_Canchas/";
    public String imgfotoperfil = url+"cuenta/fotoperfil/Imagenes_Usuario/";
}

    /*
    STRINGREQUESTEXAMPLE
        StringRequest strq = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("rta_servidor", response);
                        progreso1.dismiss();

                        if (response.equalsIgnoreCase("Ha ingresado correctamente.")){
                            Intent intentiniciar = new Intent(Login.this, Home.class);
                            startActivity(intentiniciar);
                            Toast.makeText(ctx, response,
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }

                        else {
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Login.this);
                            dialogo1.setTitle("INICIAR SESIÓN");
                            dialogo1.setMessage(response);
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    //Ejecute acciones, deje vacio para solo aceptar
                                    dialogo1.cancel();
                                    et_pass.setText(null);
                                }
                            });
                            dialogo1.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error_servidor", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();

                parametros.put("email", et_id.getText().toString());
                parametros.put("pass", et_pass.getText().toString());

                return parametros;
            }
        };
        rq.add(strq);

        */