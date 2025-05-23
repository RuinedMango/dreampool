#version 460 core
in vec4 Color;
in vec2 TexCoord;

uniform sampler2D uFontAtlasTexture;

out vec4 FragColor;

void main()
{
	//FragColor = vec4(1.0, 0.0, 0.0 ,1.0);
    FragColor = vec4(texture(uFontAtlasTexture, TexCoord).r) * Color;
}