package de.mpicbg.scicomp.scratch

import de.mpicbg.scicomp.kscript.processLines
import de.mpicbg.scicomp.kscript.processStdin

processStdin { it.split(" ")[1] }

"""sdf
adsf
asdf
""".processLines { it + 1 }



