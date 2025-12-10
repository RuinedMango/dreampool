#version 460 core
in vec4 Color;
in vec2 TexCoord;

uniform sampler2D uTexture;
uniform bool uGrayscale;
uniform bool uFlat;

out vec4 FragColor;

void main()
{
    vec4 texSample = texture(uTexture, TexCoord);
	
	if (uFlat){
		FragColor = Color;
		return;
	}
    if (uGrayscale) {
        // STB font atlas: red channel = alpha
        float alpha = texSample.r;
        FragColor = vec4(Color.rgb, Color.a * alpha);
    } else {
        // Regular RGBA or RGB image
        FragColor = texSample * Color;
    }
}
