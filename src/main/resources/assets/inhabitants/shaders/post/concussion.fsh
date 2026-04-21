#version 330

uniform sampler2D InSampler;
uniform sampler2D PrevSampler;

in vec2 texCoord;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform PhosphorConfig {
    vec4 Phosphor;
};

out vec4 fragColor;

void main() {
    vec4 current = texture(InSampler, texCoord);
    vec4 previous = texture(PrevSampler, texCoord);
    
    // Authentic 1.20 Phosphor logic:
    // It takes the maximum of the current pixel and the decayed previous pixel.
    vec3 result = max(previous.rgb * Phosphor.rgb, current.rgb);
    
    fragColor = vec4(result, 1.0);
}
