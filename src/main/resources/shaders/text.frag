#version 460 core

in vec4 color;
in vec2 texCoord;

uniform sampler2D uFontAtlasTexture;

out vec4 FragColor;

void main()
{
    FragColor = vec4(texture(uFontAtlasTexture, texCoord).r) * color;
}