using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Udid.Generator.RNUdidGenerator
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNUdidGeneratorModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNUdidGeneratorModule"/>.
        /// </summary>
        internal RNUdidGeneratorModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNUdidGenerator";
            }
        }
    }
}
