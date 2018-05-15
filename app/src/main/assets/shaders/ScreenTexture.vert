#version 300 es

layout (location = 0) in vec2 in_pos;

out vec2 v2f_texCoord;

void main() {
    v2f_texCoord = in_pos * 0.5f + 0.5f;

	gl_Position = vec4(in_pos, 0.0f, 1.0f);
}
